package dwe.holding.generic.preferences.controller;

import dwe.holding.generic.preferences.model.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {

}