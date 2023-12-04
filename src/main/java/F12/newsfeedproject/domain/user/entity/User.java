package F12.newsfeedproject.domain.user.entity;

import F12.newsfeedproject.domain.user.constant.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long userId;

  @Column(nullable = false)
  private String userName;

  @Column(nullable = false)
  private String userPassword;

  @Column(nullable = false)
  private String userEmail;

  @Column(nullable = false)
  private String userImageUrl;

  @Column(nullable = false)
  private String userIntroduce;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserRole userRole;

  private String refreshToken;

  public void updateRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public void updateUser(User modifyUser) {
    userImageUrl = modifyUser.getUserImageUrl();
    userIntroduce = modifyUser.getUserIntroduce();
  }

  public void logout() {
    refreshToken = null;
  }
}
