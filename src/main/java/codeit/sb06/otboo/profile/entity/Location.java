package codeit.sb06.otboo.profile.entity;

import codeit.sb06.otboo.profile.dto.LocationDto;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private double latitude;
    private double longitude;

    private Integer x;
    private Integer y;

    @ElementCollection
    @CollectionTable(name = "location_details", joinColumns = @JoinColumn(name = "location_id"))
    @Column(name = "location_detail")
    private List<String> locationDetails;

    @OneToOne
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;

    public static Location from(LocationDto locationDto, Profile profile) {
        return Location.builder()
            .latitude(locationDto.latitude())
            .longitude(locationDto.longitude())
            .x(locationDto.x())
            .y(locationDto.y())
            .locationDetails(locationDto.locationNames())
            .profile(profile)
            .build();
    }

    public void updateLocation(LocationDto locationDto) {
        this.latitude = locationDto.latitude();
        this.longitude = locationDto.longitude();
        this.x = locationDto.x();
        this.y = locationDto.y();
        this.locationDetails = locationDto.locationNames();
    }

}
