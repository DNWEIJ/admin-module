package dwe.holding.generic.preferences.controller;

import dwe.holding.generic.preferences.model.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UUID> {

}