package org.rentfriend.filter;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.beans.factory.annotation.Value;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileFilterRequest {

  //@Pattern(regexp = "")
  public String city;
  @Min(18)
  @Max(100)
  public String maxAge;
  @Min(18)
  @Max(100)
  public String minAge;
  @Pattern(regexp = "female|male")
  public String gender;
}
