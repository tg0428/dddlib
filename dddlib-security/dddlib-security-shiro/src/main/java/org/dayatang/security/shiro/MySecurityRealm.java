package org.dayatang.security.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.dayatang.security.domain.Permission;
import org.dayatang.security.domain.Role;
import org.dayatang.security.domain.User;

import javax.inject.Inject;

/**
 * Created by yyang on 14/11/11.
 */
public class MySecurityRealm extends AuthorizingRealm {

    @Inject
    public MySecurityRealm(CredentialsMatcher credentialsMatcher) {
        setName("MySecurityRealm");
        setAuthenticationTokenClass(UsernamePasswordToken.class);
        setCredentialsMatcher(credentialsMatcher);
    }
   

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null)
            throw new AuthorizationException("PrincipalCollection was null, which should not happen");

        if (principals.isEmpty())
            return null;

        if (principals.fromRealm(getName()).size() <= 0)
            return null;

        User user = (User) principals.fromRealm(getName()).iterator().next();
//        if (username == null)
//            return null;
//        User user = securityApplication.getUserByUsername(username);
        if (user == null)
            return null;
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        for (Role role : user.getAllRoles()) {
            info.addRole(role.getName());
        }
        for (Permission permission : user.getAllPermissions()) {
            info.addStringPermission(permission.getName());
        }
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        if (!User.authenticate(usernamePasswordToken.getUsername(),
                new String(usernamePasswordToken.getPassword()))) {
            return null;
        }
        User user = User.getByName(usernamePasswordToken.getUsername());
        return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
    }
}
