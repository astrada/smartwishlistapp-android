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
 * (build: 2015-08-03 17:34:38 UTC)
 * on 2015-08-10 at 21:16:54 UTC 
 * Modify at your own risk.
 */

package com.appspot.smart_wish_list.smartwishlist.model;

/**
 * Model definition for SmartWishListAppNotificationData.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the smartwishlist. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class SmartWishListAppNotificationData extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long errorCode;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String errorMessage;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<SmartWishListNotificationTriggerData> triggers;

  static {
    // hack to force ProGuard to consider SmartWishListNotificationTriggerData used, since otherwise it would be stripped out
    // see http://code.google.com/p/google-api-java-client/issues/detail?id=528
    com.google.api.client.util.Data.nullOf(SmartWishListNotificationTriggerData.class);
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getErrorCode() {
    return errorCode;
  }

  /**
   * @param errorCode errorCode or {@code null} for none
   */
  public SmartWishListAppNotificationData setErrorCode(java.lang.Long errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getErrorMessage() {
    return errorMessage;
  }

  /**
   * @param errorMessage errorMessage or {@code null} for none
   */
  public SmartWishListAppNotificationData setErrorMessage(java.lang.String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<SmartWishListNotificationTriggerData> getTriggers() {
    return triggers;
  }

  /**
   * @param triggers triggers or {@code null} for none
   */
  public SmartWishListAppNotificationData setTriggers(java.util.List<SmartWishListNotificationTriggerData> triggers) {
    this.triggers = triggers;
    return this;
  }

  @Override
  public SmartWishListAppNotificationData set(String fieldName, Object value) {
    return (SmartWishListAppNotificationData) super.set(fieldName, value);
  }

  @Override
  public SmartWishListAppNotificationData clone() {
    return (SmartWishListAppNotificationData) super.clone();
  }

}
