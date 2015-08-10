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
 * User data.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the smartwishlist. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class SmartWishListUserData extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean appEnabled;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String emailAddress;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean emailEnabled;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean feedEnabled;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String newPassword;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String password;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getAppEnabled() {
    return appEnabled;
  }

  /**
   * @param appEnabled appEnabled or {@code null} for none
   */
  public SmartWishListUserData setAppEnabled(java.lang.Boolean appEnabled) {
    this.appEnabled = appEnabled;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getEmailAddress() {
    return emailAddress;
  }

  /**
   * @param emailAddress emailAddress or {@code null} for none
   */
  public SmartWishListUserData setEmailAddress(java.lang.String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getEmailEnabled() {
    return emailEnabled;
  }

  /**
   * @param emailEnabled emailEnabled or {@code null} for none
   */
  public SmartWishListUserData setEmailEnabled(java.lang.Boolean emailEnabled) {
    this.emailEnabled = emailEnabled;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getFeedEnabled() {
    return feedEnabled;
  }

  /**
   * @param feedEnabled feedEnabled or {@code null} for none
   */
  public SmartWishListUserData setFeedEnabled(java.lang.Boolean feedEnabled) {
    this.feedEnabled = feedEnabled;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getNewPassword() {
    return newPassword;
  }

  /**
   * @param newPassword newPassword or {@code null} for none
   */
  public SmartWishListUserData setNewPassword(java.lang.String newPassword) {
    this.newPassword = newPassword;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPassword() {
    return password;
  }

  /**
   * @param password password or {@code null} for none
   */
  public SmartWishListUserData setPassword(java.lang.String password) {
    this.password = password;
    return this;
  }

  @Override
  public SmartWishListUserData set(String fieldName, Object value) {
    return (SmartWishListUserData) super.set(fieldName, value);
  }

  @Override
  public SmartWishListUserData clone() {
    return (SmartWishListUserData) super.clone();
  }

}
