package com.saransh.automation.pages;

/** One row of the cart table, as rendered in the UI. */
public record CartItem(int productId, String name, String price, int quantity, String total) {
}
