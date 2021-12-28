package fr.miage.bank.security;

import org.springframework.security.access.PermissionEvaluator;

public interface TargetedPermissionEvaluator extends PermissionEvaluator {
    String getTargetType();
}
