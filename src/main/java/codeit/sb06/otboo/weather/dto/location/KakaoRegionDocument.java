package codeit.sb06.otboo.weather.dto.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoRegionDocument(

    @JsonProperty("region_type")
    String regionType,   // B / H

    String code,

    @JsonProperty("address_name")
    String addressName,

    @JsonProperty("region_1depth_name")
    String region1DepthName,

    @JsonProperty("region_2depth_name")
    String region2DepthName,

    @JsonProperty("region_3depth_name")
    String region3DepthName,

    @JsonProperty("region_4depth_name")
    String region4DepthName,

    double x,
    double y
) {}