package ro.fortech.security;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "authorities", uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "authority"})})
@IdClass(Authority.AuthorityId.class)
public class Authority {


    @Id
    @JoinColumn(name = "username", nullable = false)
    @ManyToOne
    private User user;

    @Id
    private String authority;


    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class AuthorityId implements Serializable {
        private User user;

        private String authority;

        public AuthorityId() {
        }

        public AuthorityId(User user, String authority) {
            this.user = user;
            this.authority = authority;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority1 = (Authority) o;
        return Objects.equals(user, authority1.user) &&
                Objects.equals(authority, authority1.authority);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
