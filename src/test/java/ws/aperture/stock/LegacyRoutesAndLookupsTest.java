package ws.aperture.stock;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * The README promises that legacy route aliases keep working, so each alias is exercised here;
 * without these tests an alias could be dropped or mis-bound and nothing would notice. Also covers
 * lookups and not-found responses for every resource. Tests share one database, so each creates
 * uniquely named records and never assumes a list is empty.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LegacyRoutesAndLookupsTest {
  static final long MISSING = 99999999;

  @Autowired MockMvc mvc;

  ResultActions call(MockHttpServletRequestBuilder request) throws Exception {
    return mvc.perform(request.with(httpBasic("tester", "test-password")).with(csrf()));
  }

  MockHttpServletRequestBuilder body(MockHttpServletRequestBuilder request, String json) {
    return request.contentType("application/json").content(json);
  }

  long id(ResultActions result, String key) throws Exception {
    return ((Number)
            JsonPath.read(result.andReturn().getResponse().getContentAsString(), "$." + key))
        .longValue();
  }

  String companyNumber() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
  }

  long supplier() throws Exception {
    String company = companyNumber();
    return id(
        call(body(
                put("/suppliers/register"),
                "{\"name\":\"Farm " + company + "\",\"companyNumber\":\"" + company + "\"}"))
            .andExpect(status().isCreated()),
        "id");
  }

  long stockItem() throws Exception {
    return id(
        call(body(
                post("/stock"),
                "{\"name\":\"Whole milk\",\"supplierId\":" + supplier() + ",\"retailPrice\":1.10}"))
            .andExpect(status().isCreated()),
        "stockItemId");
  }

  long product() throws Exception {
    return id(
        call(body(put("/products"), "{\"name\":\"Latte\",\"desc\":\"Milk\",\"retailPrice\":3.20}"))
            .andExpect(status().isCreated()),
        "id");
  }

  long user() throws Exception {
    return id(
        call(body(
                post("/users"),
                "{\"firstName\":\"Ada\",\"lastName\":\"Lovelace\",\"email\":\""
                    + UUID.randomUUID()
                    + "@example.com\"}"))
            .andExpect(status().isCreated()),
        "id");
  }

  long newOrder(int quantity) throws Exception {
    return id(
        call(body(
                post("/customer-orders/new"),
                "{\"userId\":"
                    + user()
                    + ",\"itemRequests\":[{\"productId\":"
                    + product()
                    + ",\"quantity\":"
                    + quantity
                    + "}]}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("NEW")),
        "customerOrderId");
  }

  @Test
  void aSupplierRegisteredThroughTheLegacyRouteCanBeFetchedById() throws Exception {
    String company = companyNumber();
    long s =
        id(
            call(body(
                    put("/suppliers/register"),
                    "{\"name\":\"Fresh " + company + "\",\"companyNumber\":\"" + company + "\"}"))
                .andExpect(status().isCreated()),
            "id");
    call(get("/suppliers/" + s))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.supplierName").value("Fresh " + company))
        .andExpect(jsonPath("$.companyNumber").value(company.toUpperCase(Locale.ROOT)));
  }

  @Test
  void aSupplierWithAMalformedCompanyNumberIsRejected() throws Exception {
    for (String company : new String[] {"123", "1234567890123", "1234-5678", ""})
      call(body(
              post("/suppliers"),
              "{\"name\":\"Bad " + UUID.randomUUID() + "\",\"companyNumber\":\"" + company + "\"}"))
          .andExpect(status().isBadRequest());
  }

  @Test
  void aSupplierRemovedThroughTheLegacyRouteIsNoLongerListed() throws Exception {
    long s = supplier();
    call(get("/suppliers/all")).andExpect(jsonPath("$[*].id").value(hasItem((int) s)));
    call(delete("/suppliers/remove/" + s)).andExpect(status().isOk());
    call(get("/suppliers/all")).andExpect(jsonPath("$[?(@.id == %d)]", s).isEmpty());
    call(get("/suppliers/" + s)).andExpect(status().isNotFound());
  }

  @Test
  void everyResourceReportsAnUnknownIdAsNotFound() throws Exception {
    for (String route :
        new String[] {
          "/suppliers/", "/stock/", "/ingredients/", "/users/", "/customer-orders/", "/products/"
        }) call(get(route + MISSING)).andExpect(status().isNotFound());
    call(get("/stock/" + MISSING + "/receipts")).andExpect(status().isNotFound());
    call(get("/products/" + MISSING + "/recipe")).andExpect(status().isNotFound());
    call(get("/users/info/nobody-" + UUID.randomUUID())).andExpect(status().isNotFound());
    call(delete("/suppliers/" + MISSING)).andExpect(status().isNotFound());
    call(post("/customer-orders/" + MISSING + "/start")).andExpect(status().isNotFound());
    call(body(
            put("/stock/" + MISSING),
            "{\"incomingDate\":\"2026-09-01\",\"expiryDate\":\"2026-09-30\",\"quantity\":5}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void aProductCreatedThroughTheLegacyRouteAppearsInTheCatalogue() throws Exception {
    long p = product();
    call(get("/products"))
        .andExpect(jsonPath("$[?(@.id == %d)].productName", p).value(contains("Latte")))
        .andExpect(jsonPath("$[?(@.id == %d)].retailPrice", p).value(contains(3.20)));
  }

  @Test
  void aDeliveryRecordedThroughTheLegacyRouteIsListedAsAReceipt() throws Exception {
    long item = stockItem();
    call(body(
            put("/stock/" + item),
            "{\"incomingDate\":\"2026-09-01\",\"expiryDate\":\"2026-09-30\","
                + "\"quantity\":12,\"storage\":\"CHILLED\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.stockItemId").value(item))
        .andExpect(jsonPath("$.quantity").value(12))
        .andExpect(jsonPath("$.expireDate").value("2026-09-30"))
        .andExpect(jsonPath("$.storage").value("CHILLED"));
    call(get("/stock/" + item + "/receipts"))
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].storage").value("CHILLED"));
  }

  @Test
  void aDeliveryOfNothingOrForAnotherItemIsRejectedAndNotRecorded() throws Exception {
    long item = stockItem(), other = stockItem();
    String dates = "\"incomingDate\":\"2026-09-01\",\"expiryDate\":\"2026-09-30\"";
    for (String json :
        new String[] {
          "{" + dates + ",\"quantity\":0}",
          "{" + dates + ",\"quantity\":-1}",
          "{" + dates + ",\"quantity\":1,\"stockItemId\":" + other + "}",
          "{\"expiryDate\":\"2026-09-30\",\"quantity\":1}"
        }) call(body(put("/stock/" + item), json)).andExpect(status().isBadRequest());
    call(get("/stock/" + item + "/receipts")).andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void anOrderCanBeDrivenFromNewToCompletedThroughTheLegacyRoutes() throws Exception {
    long o = newOrder(2);
    call(post("/customer-orders/start/" + o))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("STARTED"));
    call(post("/customer-orders/complete/" + o)).andExpect(status().isOk());
    call(get("/customer-orders/" + o))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("COMPLETED"))
        .andExpect(jsonPath("$.orderItems[0].quantity").value(2))
        .andExpect(jsonPath("$.total").value(6.40));
    call(get("/customer-orders"))
        .andExpect(jsonPath("$[*].customerOrderId").value(hasItem((int) o)));
  }

  @Test
  void bothCancelAliasesAndTheLegacyDeleteRouteStillWork() throws Exception {
    long o = newOrder(1);
    call(post("/customer-orders/cancle/" + o))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CANCELLED"));
    call(post("/customer-orders/cancel/" + newOrder(1)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CANCELLED"));
    call(post("/customer-orders/delete/" + o))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DELETED"));
  }

  @Test
  void aRecipeReplacedThroughTheLegacyRouteIsReturned() throws Exception {
    long p = product();
    long ingredient =
        id(
            call(body(post("/ingredients"), "{\"name\":\"Espresso\",\"unit\":\"ML\"}"))
                .andExpect(status().isCreated()),
            "id");
    call(body(
            post("/products/update-recipe/" + p),
            "[{\"ingredientId\":" + ingredient + ",\"quantity\":30}]"))
        .andExpect(status().isOk());
    call(get("/products/" + p + "/recipe"))
        .andExpect(jsonPath("$.recipeItems[0].ingredientId").value(ingredient))
        .andExpect(jsonPath("$.recipeItems[0].quantity").value(30));
  }

  @Test
  void staffCanBeFoundByTheirGeneratedUserName() throws Exception {
    long u = user();
    call(get("/users/info/staff-" + u))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(u))
        .andExpect(jsonPath("$.userName").value("staff-" + u));
    call(get("/users/ids")).andExpect(jsonPath("$[*].id").value(hasItem((int) u)));
  }
}
