package com.serenitydojo.playwright.search;

import com.serenitydojo.playwright.PlaywrightTestMultiThread;
import com.serenitydojo.playwright.PlaywrightTestSingleThread;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.List;


public class SearchProducts extends PlaywrightTestMultiThread {

    Cart cart;
    Search search;
    ProductList productList;
    ProductItem productItem;

    @BeforeEach
    void beforeEach() {
        cart = new Cart(page);
        search = new Search(page);
        productList = new ProductList(page);
        productItem = new ProductItem(page);

//        context.tracing().start(
//                new Tracing.StartOptions()
//                        .setScreenshots(true)
//                        .setSnapshots(true)
//                        .setSources(true)
//        );
    }

//    @AfterEach
//    void afterEach(TestInfo info, BrowserContext browserContext) {
//
//        String traceTargetName = "target/trace/trace_"
//                + info.getDisplayName().replace(" ", "_") + ".zip";
//
//        context.tracing().stop(
//                new Tracing.StopOptions()
//                        .setPath(Paths.get(traceTargetName))
//        );
//    }

    @DisplayName("Search for item")
    @Test
    public void searchForItem() {

        search.by("ruler");

        List<ProductListItem> productListItems = productList.getNames();

        Assertions.assertThat(productListItems).isNotEmpty();
        Assertions.assertThat(productListItems.size()).isEqualTo(1);
        Assertions.assertThat(productListItems).extracting(ProductListItem::name)
                .contains("Square Ruler");
    }

    @DisplayName("Adding items to cart")
    @Test
    public void shouldAddItemsToCart() {

        CartListItem expected = new CartListItem("Combination Pliers", 3, 14.15, 42.45);

        search.by("Pliers");
        productItem.viewProductDetails("Combination Pliers");

        productItem.setQuantity(3);
        productItem.addToCart();

        cart.viewCart();
        List<CartListItem> items = cart.getLineItems();
        System.out.println(items);

        Assertions.assertThat(items)
                .hasSize(1)
                .first()
                .satisfies(item -> {
                            Assertions.assertThat(item.name()).isEqualTo(expected.name());
                            Assertions.assertThat(item.quantity()).isEqualTo(expected.quantity());
                            Assertions.assertThat(item.price()).isEqualTo(expected.price());
                            Assertions.assertThat(item.total()).isEqualTo(expected.total());
                        }
                );
    }



}

