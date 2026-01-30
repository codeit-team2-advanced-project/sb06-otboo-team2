package codeit.sb06.otboo.user.repository;

import codeit.sb06.otboo.user.dto.request.UserSliceRequest;
import codeit.sb06.otboo.user.entity.User;
import org.springframework.data.domain.Slice;

public interface UserRepositoryCustom {

    Slice<User> findUsersBySlice(UserSliceRequest userSliceRequest);

}
