package com.skkutable.domain;

public enum Role {
  USER, ADMIN, HOST;

  /**
   * Converts the enum constant to a string representation suitable for use as a Spring Security authority.
   *
   * @return the string representation of the role, prefixed with "ROLE_"
   */
  public String toAuthority() {
    return "ROLE_" + this.name();
  }
}
