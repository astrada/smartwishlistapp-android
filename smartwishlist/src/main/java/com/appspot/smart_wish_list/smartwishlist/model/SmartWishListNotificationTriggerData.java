/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-03-26 20:30:19 UTC)
 * on 2015-06-07 at 20:12:14 UTC 
 * Modify at your own risk.
 */

package com.appspot.smart_wish_list.smartwishlist.model;

/**
 * Model definition for SmartWishListNotificationTriggerData.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the smartwishlist. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class SmartWishListNotificationTriggerData extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean availability;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double creationDate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean enabled;

  /**
   * Item data message.
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private SmartWishListItemData item;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double priceThreshold;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean soldByAmazon;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long wishListIndex;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getAvailability() {
    return availability;
  }

  /**
   * @param availability availability or {@code null} for none
   */
  public SmartWishListNotificationTriggerData setAvailability(java.lang.Boolean availability) {
    this.availability = availability;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getCreationDate() {
    return creationDate;
  }

  /**
   * @param creationDate creationDate or {@code null} for none
   */
  public SmartWishListNotificationTriggerData setCreationDate(java.lang.Double creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getEnabled() {
    return enabled;
  }

  /**
   * @param enabled enabled or {@code null} for none
   */
  public SmartWishListNotificationTriggerData setEnabled(java.lang.Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Item data message.
   * @return value or {@code null} for none
   */
  public SmartWishListItemData getItem() {
    return item;
  }

  /**
   * Item data message.
   * @param item item or {@code null} for none
   */
  public SmartWishListNotificationTriggerData setItem(SmartWishListItemData item) {
    this.item = item;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getPriceThreshold() {
    return priceThreshold;
  }

  /**
   * @param priceThreshold priceThreshold or {@code null} for none
   */
  public SmartWishListNotificationTriggerData setPriceThreshold(java.lang.Double priceThreshold) {
    this.priceThreshold = priceThreshold;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getSoldByAmazon() {
    return soldByAmazon;
  }

  /**
   * @param soldByAmazon soldByAmazon or {@code null} for none
   */
  public SmartWishListNotificationTriggerData setSoldByAmazon(java.lang.Boolean soldByAmazon) {
    this.soldByAmazon = soldByAmazon;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getWishListIndex() {
    return wishListIndex;
  }

  /**
   * @param wishListIndex wishListIndex or {@code null} for none
   */
  public SmartWishListNotificationTriggerData setWishListIndex(java.lang.Long wishListIndex) {
    this.wishListIndex = wishListIndex;
    return this;
  }

  @Override
  public SmartWishListNotificationTriggerData set(String fieldName, Object value) {
    return (SmartWishListNotificationTriggerData) super.set(fieldName, value);
  }

  @Override
  public SmartWishListNotificationTriggerData clone() {
    return (SmartWishListNotificationTriggerData) super.clone();
  }

}
