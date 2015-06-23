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
 * on 2015-06-23 at 21:47:15 UTC 
 * Modify at your own risk.
 */

package com.appspot.smart_wish_list.smartwishlist.model;

/**
 * Model definition for SmartWishListFeedbackData.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the smartwishlist. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class SmartWishListFeedbackData extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String description;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean followup;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDescription() {
    return description;
  }

  /**
   * @param description description or {@code null} for none
   */
  public SmartWishListFeedbackData setDescription(java.lang.String description) {
    this.description = description;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getFollowup() {
    return followup;
  }

  /**
   * @param followup followup or {@code null} for none
   */
  public SmartWishListFeedbackData setFollowup(java.lang.Boolean followup) {
    this.followup = followup;
    return this;
  }

  @Override
  public SmartWishListFeedbackData set(String fieldName, Object value) {
    return (SmartWishListFeedbackData) super.set(fieldName, value);
  }

  @Override
  public SmartWishListFeedbackData clone() {
    return (SmartWishListFeedbackData) super.clone();
  }

}
