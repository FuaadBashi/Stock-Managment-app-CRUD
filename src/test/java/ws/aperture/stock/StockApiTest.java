package ws.aperture.stock;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StockApiTest {

    @Autowired private MockMvc mvc;

    private ResultActions send(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request,
            String json)
            throws Exception {
        return mvc.perform(request.contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private long registerSupplier() throws Exception {
        String body =
                send(
                                put("/suppliers/register"),
                                """
                                {"name": "Fresh Farms Ltd", "companyNumber": "12345678"}
                                """)
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    @Test
    void aRegisteredSupplierCanBeFetchedById() throws Exception {
        long id = registerSupplier();

        mvc.perform(get("/suppliers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supplierName").value("Fresh Farms Ltd"))
                .andExpect(jsonPath("$.companyNumber").value("12345678"));
    }

    @Test
    void aSupplierWithAnInvalidCompanyNumberIsRejected() throws Exception {
        send(
                        put("/suppliers/register"),
                        """
                {"name": "Fresh Farms Ltd", "companyNumber": "123"}
                """)
                .andExpect(status().isBadRequest());
    }

    @Test
    void anUnknownSupplierIsNotFound() throws Exception {
        mvc.perform(get("/suppliers/999")).andExpect(status().isNotFound());
    }

    @Test
    void aRemovedSupplierIsNoLongerListed() throws Exception {
        long id = registerSupplier();

        mvc.perform(delete("/suppliers/remove/" + id)).andExpect(status().isOk());

        mvc.perform(get("/suppliers/all")).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void aProductWithANonPositivePriceIsRejected() throws Exception {
        send(
                        put("/products"),
                        """
                {"name": "Latte", "desc": "Coffee", "retailPrice": 0}
                """)
                .andExpect(status().isBadRequest());
    }

    @Test
    void aCreatedProductAppearsInTheCatalogue() throws Exception {
        send(
                        put("/products"),
                        """
                {"name": "Latte", "desc": "Coffee with milk", "retailPrice": 3.2}
                """)
                .andExpect(status().isOk());

        mvc.perform(get("/products"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].productName").value("Latte"));
    }

    @Test
    void aStockItemIsSavedAndADeliveryCanBeRecordedAgainstIt() throws Exception {
        long supplierId = registerSupplier();
        String item =
                send(
                                post("/stock"),
                                """
                                {"name": "Whole milk", "supplierId": %d, "retailPrice": 1.1, "desc": "1 litre"}
                                """
                                        .formatted(supplierId))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.stockItemId", notNullValue()))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        long itemId = ((Number) JsonPath.read(item, "$.stockItemId")).longValue();

        mvc.perform(get("/stock")).andExpect(jsonPath("$", hasSize(1)));

        send(
                        put("/stock/" + itemId),
                        """
                {"expiryDate": "2030-01-31", "quantity": 12, "storage": "CHILLED"}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.quantity").value(12.0))
                .andExpect(jsonPath("$.expireDate").value("2030-01-31"));
    }

    @Test
    void aDeliveryOfNothingIsRejected() throws Exception {
        long supplierId = registerSupplier();
        String item =
                send(
                                post("/stock"),
                                """
                        {"name": "Oat milk", "supplierId": %d, "retailPrice": 1.5, "desc": ""}
                        """
                                        .formatted(supplierId))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        long itemId = ((Number) JsonPath.read(item, "$.stockItemId")).longValue();

        send(
                        put("/stock/" + itemId),
                        """
                {"expiryDate": "2030-01-31", "quantity": 0}
                """)
                .andExpect(status().isBadRequest());
    }

    @Test
    void aDeliveryForAnUnknownStockItemIsNotFound() throws Exception {
        send(
                        put("/stock/999"),
                        """
                {"expiryDate": "2030-01-31", "quantity": 5}
                """)
                .andExpect(status().isNotFound());
    }

    private long idFrom(ResultActions result, String path) throws Exception {
        String body = result.andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, path)).longValue();
    }

    @Test
    void anOrderMovesFromNewToStartedToCompleted() throws Exception {
        long userId =
                idFrom(
                        send(
                                        post("/users"),
                                        """
                                {"firstName": "Ada", "lastName": "Lovelace", "userName": "ada",
                                 "email": "ada@example.com"}
                                """)
                                .andExpect(status().isOk()),
                        "$.id");
        long productId =
                idFrom(
                        send(
                                put("/products"),
                                """
                                {"name": "Latte", "desc": "Coffee", "retailPrice": 3.2}
                                """),
                        "$.id");

        long orderId =
                idFrom(
                        send(
                                        post("/customer-orders/new"),
                                        """
                                {"userId": %d, "itemRequests": [{"productId": %d, "quantity": 2}]}
                                """
                                                .formatted(userId, productId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("NEW")),
                        "$.customerOrderId");

        mvc.perform(post("/customer-orders/start/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("STARTED"));
        mvc.perform(post("/customer-orders/complete/" + orderId)).andExpect(status().isOk());

        mvc.perform(get("/customer-orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(2));
    }

    @Test
    void anUnknownOrderIsNotFound() throws Exception {
        mvc.perform(get("/customer-orders/999")).andExpect(status().isNotFound());
    }
}
