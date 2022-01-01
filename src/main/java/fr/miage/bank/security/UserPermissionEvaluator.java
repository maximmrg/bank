package fr.miage.bank.security;

import fr.miage.bank.entity.User;
import fr.miage.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class UserPermissionEvaluator implements TargetedPermissionEvaluator {

    private final UserService userService;

    @Override
    public String getTargetType(){
        return User.class.getSimpleName();
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission){
        throw new UnsupportedOperationException("Not supported by this PersmisionEvaluator: " + UserPermissionEvaluator.class);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

        boolean authorized = false;

        String perm = permission.toString();

        User springUser = userService.getUserByEmail(authentication.getName()).get();
        User user = userService.findById(targetId.toString()).get();

        switch (perm) {
            case "MANAGE_USER" :
                authorized = springUser.getId().equals(user.getId());
                System.out.println("J'autorise (switch) : " + authorized);
                break;

            default:
                break;
        }

        System.out.println("J'autorise : " + authorized);

        return authorized;
    }
}
