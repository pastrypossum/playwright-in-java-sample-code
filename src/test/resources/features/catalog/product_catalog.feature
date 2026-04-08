Feature: Product Catalog

  As a customer I want to search, filter and sort products in the catalog.
  This is so I that I can find the products I am interested in quickly.

  Background:
    Given Sally is on the home page

  Rule: Customer searches for products by name

    Example: Search by specific item name
      When she searches for "Combination Pliers"
      Then the following products should be displayed:
        | Product Name       | Price |
        | Combination Pliers | 14.15 |

    Example: Search by broad item name
      When she searches for "Pliers"
      Then the following products should be displayed:
        | Product Name       | Price |
        | Pliers             | 12.01 |
        | Combination Pliers | 14.15 |
        | Long Nose Pliers   | 14.24 |
        | Slip Joint Pliers  | 9.17  |

    Example: Search by unknown item name
      When she searches for "Axe"
      Then no products should be displayed
      And a message "There are no products found." should be displayed


  Rule: Customer narrows search results by category

    Example: Filter search results by category
      When she searches for "Saw"
      And filters by the category "Hand Saw"
      Then the following products should be displayed:
        | Product Name | Price |
        | Wood Saw     | 12.18 |

  Rule: Customer sorts search results

    Example: Sort search results by ascending name
      When she searches for "Pliers"
      And sorts by "Name (A - Z)"
      Then the following products should be displayed in order:
        | Product Name       | Price |
        | Combination Pliers | 14.15 |
        | Long Nose Pliers   | 14.24 |
        | Pliers             | 12.01 |
        | Slip Joint Pliers  | 9.17  |

    Example: Sort search results by descending name
      When she searches for "Pliers"
      And sorts by "Name (Z - A)"
      Then the following products should be displayed in order:
        | Product Name       | Price |
        | Slip Joint Pliers  | 9.17  |
        | Pliers             | 12.01 |
        | Long Nose Pliers   | 14.24 |
        | Combination Pliers | 14.15 |

    Example: Sort search results by ascending price
      When she searches for "Pliers"
      And sorts by "Price (Low - High)"
      Then the following products should be displayed in order:
        | Product Name       | Price |
        | Slip Joint Pliers  | 9.17  |
        | Pliers             | 12.01 |
        | Combination Pliers | 14.15 |
        | Long Nose Pliers   | 14.24 |

    Example: Sort search results by descending name
      When she searches for "Pliers"
      And sorts by "Price (High - Low)"
      Then the following products should be displayed in order:
        | Product Name       | Price |
        | Long Nose Pliers   | 14.24 |
        | Combination Pliers | 14.15 |
        | Pliers             | 12.01 |
        | Slip Joint Pliers  | 9.17  |