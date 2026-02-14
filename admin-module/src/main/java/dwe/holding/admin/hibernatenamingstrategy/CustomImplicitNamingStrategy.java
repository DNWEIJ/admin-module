package dwe.holding.admin.hibernatenamingstrategy;

import org.hibernate.boot.model.naming.*;

import java.util.stream.Collectors;

public class CustomImplicitNamingStrategy
        extends ImplicitNamingStrategyJpaCompliantImpl {

    @Override
    public Identifier determineForeignKeyName(ImplicitForeignKeyNameSource source) {
        String table = source.getTableName().getText();
        String refTable = source.getReferencedTableName().getText();
        String cols = source.getColumnNames().stream()
                .map(Identifier::getText)
                .collect(Collectors.joining("_"));
        String name = "fk_" + table + "_" + refTable + "__" + cols;
        return toIdentifier(name.replaceAll("(?i)[aeiou]", ""), source.getBuildingContext());
    }

    @Override
    public Identifier determineUniqueKeyName(ImplicitUniqueKeyNameSource source) {
        String table = source.getTableName().getText();
        String cols = source.getColumnNames().stream()
                .map(Identifier::getText)
                .collect(Collectors.joining("_"));
        String name = "uk_" + table + "__" + cols;
        return toIdentifier(name.replaceAll("(?i)[aeiou]", ""), source.getBuildingContext());
    }

    @Override
    public Identifier determineIndexName(ImplicitIndexNameSource source) {
        String table = source.getTableName().getText();
        String cols = source.getColumnNames().stream()
                .map(Identifier::getText)
                .collect(Collectors.joining("_"));
        String name = "idx_" + table + "__" + cols;
        return toIdentifier(name.replaceAll("(?i)[aeiou]", ""), source.getBuildingContext());
    }
}