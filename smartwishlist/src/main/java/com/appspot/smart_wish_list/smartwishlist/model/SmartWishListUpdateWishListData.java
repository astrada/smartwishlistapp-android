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
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2016-05-27 16:00:31 UTC)
 * on 2016-06-03 at 19:46:25 UTC 
 * Modify at your own risk.
 */

package com.appspot.smart_wish_list.smartwishlist.model;

/**
 * Update wish list data.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the smartwishlist. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class SmartWishListUpdateWishListData extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long index;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long originalIndex;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getIndex() {
    return index;
  }

  /**
   * @param index index or {@code null} for none
   */
  public SmartWishListUpdateWishListData setIndex(java.lang.Long index) {
    this.index = index;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getName() {
    return name;
  }

  /**
   * @param name name or {@code null} for none
   */
  public SmartWishListUpdateWishListData setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getOriginalIndex() {
    return originalIndex;
  }

  /**
   * @param originalIndex originalIndex or {@code null} for none
   */
  public SmartWishListUpdateWishListData setOriginalIndex(java.lang.Long originalIndex) {
    this.originalIndex = originalIndex;
    return this;
  }

  @Override
  public SmartWishListUpdateWishListData set(String fieldName, Object value) {
    return (SmartWishListUpdateWishListData) super.set(fieldName, value);
  }

  @Override
  public SmartWishListUpdateWishListData clone() {
    return (SmartWishListUpdateWishListData) super.clone();
  }

}
