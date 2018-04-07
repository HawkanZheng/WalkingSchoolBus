package project.cmpt276.model.walkingschoolbus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

import project.cmpt276.server.walkingschoolbus.WGServerProxy;

/**
 * Created by Hawkan Zheng on 4/3/2018.
 * PermissionRequest object class
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionRequest {

    //variables
        protected Long id;
        protected String href;
        private String action;
        private WGServerProxy.PermissionStatus status;
        private User userA;
        private User userB;
        private Group groupG;
        private User requestingUser;
        private Set<Authorizor> authorizors;
        private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public WGServerProxy.PermissionStatus getStatus() {
        return status;
    }

    public void setStatus(WGServerProxy.PermissionStatus status) {
        this.status = status;
    }

    public User getUserA() {
        return userA;
    }

    public void setUserA(User userA) {
        this.userA = userA;
    }

    public User getUserB() {
        return userB;
    }

    public void setUserB(User userB) {
        this.userB = userB;
    }

    public Group getGroupG() {
        return groupG;
    }

    public void setGroupG(Group groupG) {
        this.groupG = groupG;
    }

    public User getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(User requestingUser) {
        this.requestingUser = requestingUser;
    }

    public Set<Authorizor> getAuthorizors() {
        return authorizors;
    }

    public void setAuthorizors(Set<Authorizor> authorizors) {
        this.authorizors = authorizors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonIgnore
    public String toPermissionListString() {
        return message;
    }



//Authorizor class
        public static class Authorizor {
            private Long id;
            private Set<User> users;
            private WGServerProxy.PermissionStatus status;
            private User whoApprovedOrDenied;

            public Long getId() {
                return id;
            }

            public void setId(Long id) {
                this.id = id;
            }

            public Set<User> getUsers() {
                return users;
            }

            public void setUsers(Set<User> users) {
                this.users = users;
            }

            public WGServerProxy.PermissionStatus getStatus() {
                return status;
            }

            public void setStatus(WGServerProxy.PermissionStatus status) {
                this.status = status;
            }

            public User getWhoApprovedOrDenied() {
                return whoApprovedOrDenied;
            }

            public void setWhoApprovedOrDenied(User whoApprovedOrDenied) {
                this.whoApprovedOrDenied = whoApprovedOrDenied;
            }


        }
    }
