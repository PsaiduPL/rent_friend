package org.rentfriend.filter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileFilterRequest {
  public String city;
  public String maxAge;
  public String minAge;
  public String gender;
}
