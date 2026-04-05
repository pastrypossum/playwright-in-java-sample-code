package com.serenitydojo.playwright.api;

public class MockAPIResponse {

    public static final String RESPONSE = """
                {
                "current_page": 1,
                "data": [
                    {
                        "id": "01KNC2D020HHDWSYXBEH1DQ0S9",
                        "name": "Laser Gun",
                        "description": "In elementum arcu vel pellentesque dignissim. Praesent est nisl, eleifend a mi at, tempor vehicula libero. Etiam sed tortor luctus, fringilla leo sed, dignissim dui. Integer interdum nunc ac mauris fermentum, nec dapibus ex facilisis. Proin eleifend blandit ipsum. Cras iaculis eget sem ut ornare. Morbi eleifend elementum elit quis ornare. Integer a nulla sit amet dui varius auctor scelerisque at lectus. Donec vitae ipsum ullamcorper, rutrum ex vel, suscipit nisi. Suspendisse potenti. Cras condimentum erat vel imperdiet gravida.",
                        "price": 99.99,
                        "is_location_offer": false,
                        "is_rental": false,
                        "co2_rating": "C",
                        "in_stock": true,
                        "is_eco_friendly": false,
                        "product_image": {
                            "id": "01KNC2D00E263PPMA5BPKWF34S",
                            "by_name": "Markus Spiske",
                            "by_url": "https:\\/\\/unsplash.com\\/@markusspiske",
                            "source_name": "Unsplash",
                            "source_url": "https:\\/\\/unsplash.com\\/photos\\/brown-ruler-with-stand-pwpVGQ-A5qI",
                            "file_name": "measure04.avif",
                            "title": "Square Ruler"
                        },
                        "category": {
                            "id": "01KNC2D005CHT52T8Q9M7FZ74M",
                            "name": "Measures"
                        },
                        "brand": {
                            "id": "01KNC2CZP1W62V3CR46J5849NA",
                            "name": "ForgeFlex Tools"
                        }
                    }
                ],
                "from": 1,
                "last_page": 1,
                "per_page": 9,
                "to": 1,
                "total": 1
            }
            """;
}
