package ws.aperture.stock;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {
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

  long product() throws Exception {
    return id(
        call(body(
                post("/products"), "{\"name\":\"Coffee\",\"desc\":\"Fresh\",\"retailPrice\":2.35}"))
            .andExpect(status().isCreated()),
        "id");
  }

  long user() throws Exception {
    return id(
        call(body(
                post("/users"),
                "{\"firstName\":\"Test\",\"lastName\":\"User\",\"email\":\""
                    + UUID.randomUUID()
                    + "@example.com\"}"))
            .andExpect(status().isCreated()),
        "id");
  }

  String order(long user, long product, int quantity) {
    return "{\"userId\":"
        + user
        + ",\"itemRequests\":[{\"productId\":"
        + product
        + ",\"quantity\":"
        + quantity
        + "}]}";
  }

  @Test
  void authenticationAndCsrfAreRequired() throws Exception {
    mvc.perform(get("/products")).andExpect(status().isUnauthorized());
    mvc.perform(body(post("/products"), "{}").with(httpBasic("tester", "test-password")))
        .andExpect(status().isForbidden());
    mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    call(get("/csrf")).andExpect(status().isOk()).andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  void invalidRequestsReturnClientErrors() throws Exception {
    call(body(post("/products"), "{\"name\":\"\",\"retailPrice\":-1}"))
        .andExpect(status().isBadRequest());
    call(body(post("/products"), "{\"id\":1,\"name\":\"X\",\"retailPrice\":1}"))
        .andExpect(status().isBadRequest());
    call(body(post("/products"), "{\"name\":\"X\",\"retailPrice\":1.001}"))
        .andExpect(status().isBadRequest());
    call(get("/products/99999999")).andExpect(status().isNotFound());
    call(body(post("/customer-orders"), "{\"userId\":1,\"itemRequests\":[null]}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void recipesReplaceAtomicallyAndProtectReferencedIngredients() throws Exception {
    long p = product();
    long a =
        id(
            call(body(post("/ingredients"), "{\"name\":\"Flour\",\"unit\":\"G\"}"))
                .andExpect(status().isCreated()),
            "id");
    long b =
        id(
            call(body(post("/ingredients"), "{\"name\":\"Water\",\"unit\":\"ML\"}"))
                .andExpect(status().isCreated()),
            "id");
    String route = "/products/" + p + "/recipe";
    call(body(put(route), "[{\"ingredientId\":" + a + ",\"quantity\":10}]"))
        .andExpect(status().isOk());
    call(body(put(route), "[{\"ingredientId\":" + b + ",\"quantity\":20}]"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeItems.length()").value(1))
        .andExpect(jsonPath("$.recipeItems[0].ingredientId").value(b));
    call(body(put(route), "[{\"ingredientId\":99999999,\"quantity\":20}]"))
        .andExpect(status().isNotFound());
    call(get(route)).andExpect(jsonPath("$.recipeItems[0].ingredientId").value(b));
    call(body(put(route), "[null]")).andExpect(status().isBadRequest());
    call(body(put(route), "[{\"ingredientId\":" + b + ",\"quantity\":0}]"))
        .andExpect(status().isBadRequest());
    call(delete("/ingredients/" + b)).andExpect(status().isConflict());
    call(delete("/ingredients/" + a)).andExpect(status().isNoContent());
  }

  @Test
  void ordersKeepPricesAndEnforceLifecycle() throws Exception {
    long p = product(), u = user();
    long o =
        id(
            call(body(post("/customer-orders"), order(u, p, 3)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(7.05)),
            "customerOrderId");
    String route = "/customer-orders/" + o;
    call(body(put(route), order(u, p, 4)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(9.40));
    call(body(put("/products/" + p), "{\"name\":\"Coffee\",\"retailPrice\":99.99}"))
        .andExpect(status().isOk());
    call(get(route)).andExpect(jsonPath("$.total").value(9.40));
    call(post(route + "/complete")).andExpect(status().isConflict());
    call(post(route + "/start")).andExpect(status().isOk());
    call(post(route + "/start")).andExpect(status().isOk());
    call(body(put(route), order(u, p, 1))).andExpect(status().isConflict());
    call(post(route + "/complete")).andExpect(status().isOk());
    call(post(route + "/cancel")).andExpect(status().isConflict());
    call(delete(route)).andExpect(status().isConflict());
    call(delete("/users/" + u)).andExpect(status().isConflict());
    call(delete("/products/" + p)).andExpect(status().isOk());
    call(get(route)).andExpect(jsonPath("$.total").value(9.40));
    call(body(post("/customer-orders"), order(u, p, 1))).andExpect(status().isConflict());
  }

  @Test
  void stockReceiptsPersistAndProtectHistory() throws Exception {
    String company = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    long s =
        id(
            call(body(
                    post("/suppliers"),
                    "{\"name\":\"" + company + "\",\"companyNumber\":\"" + company + "\"}"))
                .andExpect(status().isCreated()),
            "id");
    long item =
        id(
            call(body(
                    post("/stock"),
                    "{\"name\":\"Beans\",\"supplierId\":" + s + ",\"retailPrice\":12.50}"))
                .andExpect(status().isCreated()),
            "stockItemId");
    call(get("/stock/" + item)).andExpect(status().isOk());
    String route = "/stock/" + item + "/receipts";
    call(body(
            post(route),
            "{\"incomingDate\":\"2026-09-01\",\"expiryDate\":\"2026-10-01\",\"quantity\":5.125}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.quantity").value(5.125));
    call(body(
            post(route),
            "{\"incomingDate\":\"2026-09-01\",\"expiryDate\":\"2026-08-01\",\"quantity\":1}"))
        .andExpect(status().isBadRequest());
    call(get(route)).andExpect(jsonPath("$.length()").value(1));
    call(delete("/stock/" + item)).andExpect(status().isConflict());
    call(delete("/suppliers/" + s)).andExpect(status().isConflict());
  }

  @Test
  void duplicateEmailsAndCompaniesAreRejected() throws Exception {
    String email = UUID.randomUUID() + "@example.com";
    String request = "{\"firstName\":\"A\",\"lastName\":\"B\",\"email\":\"" + email + "\"}";
    call(body(post("/users"), request)).andExpect(status().isCreated());
    call(body(post("/users"), request.replace(email, email.toUpperCase(java.util.Locale.ROOT))))
        .andExpect(status().isConflict());
    String company = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    call(body(
            post("/suppliers"),
            "{\"name\":\"Company " + company + "\",\"companyNumber\":\"" + company + "\"}"))
        .andExpect(status().isCreated());
    call(body(
            post("/suppliers"),
            "{\"name\":\"Other Company\",\"companyNumber\":\""
                + company.toUpperCase(java.util.Locale.ROOT)
                + "\"}"))
        .andExpect(status().isConflict());
  }

  @Test
  void failedOrderEditLeavesOriginalItems() throws Exception {
    long u = user(), p = product();
    long o =
        id(
            call(body(post("/customer-orders"), order(u, p, 2))).andExpect(status().isCreated()),
            "customerOrderId");
    String route = "/customer-orders/" + o;
    call(body(put(route), order(u, 99999999, 1))).andExpect(status().isNotFound());
    call(get(route))
        .andExpect(jsonPath("$.orderItems[0].productId").value(p))
        .andExpect(jsonPath("$.total").value(4.70));
    call(body(put(route), order(user(), p, 1))).andExpect(status().isConflict());
    call(post(route + "/cancel")).andExpect(status().isOk());
    call(delete(route)).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DELETED"));
    call(get(route)).andExpect(jsonPath("$.orderItems.length()").value(1));
  }

  @Test
  void duplicateOrderLinesAndRecipeItemsAreRejected() throws Exception {
    long u = user(), p = product();
    String line = "{\"productId\":" + p + ",\"quantity\":1}";
    call(body(
            post("/customer-orders"),
            "{\"userId\":" + u + ",\"itemRequests\":[" + line + "," + line + "]}"))
        .andExpect(status().isBadRequest());
    long ingredient =
        id(
            call(body(post("/ingredients"), "{\"name\":\"Sugar\",\"unit\":\"G\"}"))
                .andExpect(status().isCreated()),
            "id");
    String recipe = "{\"ingredientId\":" + ingredient + ",\"quantity\":1}";
    call(body(put("/products/" + p + "/recipe"), "[" + recipe + "," + recipe + "]"))
        .andExpect(status().isBadRequest());
    call(get("/products/" + p + "/recipe")).andExpect(jsonPath("$.recipeItems.length()").value(0));
  }

  @Test
  void independentCrudResourcesCanBeUpdatedAndDeleted() throws Exception {
    long u = user();
    call(body(
            put("/users/" + u),
            "{\"firstName\":\"Updated\",\"lastName\":\"Staff\",\"email\":\""
                + UUID.randomUUID()
                + "@example.com\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Updated"));
    call(delete("/users/" + u)).andExpect(status().isOk());
    call(get("/users/" + u)).andExpect(status().isNotFound());
    long ingredient =
        id(
            call(body(post("/ingredients"), "{\"name\":\"Milk\",\"unit\":\"ML\"}"))
                .andExpect(status().isCreated()),
            "id");
    call(body(put("/ingredients/" + ingredient), "{\"name\":\"Oat milk\",\"unit\":\"ML\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Oat milk"));
    call(delete("/ingredients/" + ingredient)).andExpect(status().isNoContent());
  }

  @Test
  void concurrentCompletionAndCancellationCannotBothSucceed() throws Exception {
    long o =
        id(
            call(body(post("/customer-orders"), order(user(), product(), 1)))
                .andExpect(status().isCreated()),
            "customerOrderId");
    String route = "/customer-orders/" + o;
    call(post(route + "/start")).andExpect(status().isOk());
    var ready = new java.util.concurrent.CountDownLatch(2);
    var start = new java.util.concurrent.CountDownLatch(1);
    try (var executor = java.util.concurrent.Executors.newFixedThreadPool(2)) {
      var futures =
          java.util.stream.Stream.of("complete", "cancel")
              .map(
                  action ->
                      executor.submit(
                          () -> {
                            ready.countDown();
                            if (!start.await(5, java.util.concurrent.TimeUnit.SECONDS))
                              throw new IllegalStateException("Start timed out");
                            return call(post(route + "/" + action))
                                .andReturn()
                                .getResponse()
                                .getStatus();
                          }))
              .toList();
      org.junit.jupiter.api.Assertions.assertTrue(
          ready.await(5, java.util.concurrent.TimeUnit.SECONDS));
      start.countDown();
      var results = new java.util.ArrayList<Integer>();
      for (var future : futures) results.add(future.get(10, java.util.concurrent.TimeUnit.SECONDS));
      java.util.Collections.sort(results);
      org.junit.jupiter.api.Assertions.assertEquals(java.util.List.of(200, 409), results);
    }
  }
}
