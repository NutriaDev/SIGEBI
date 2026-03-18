package equipment.constants;

public class ErrorTitles {

    // General errors
    public static final String INTERNAL_ERROR = "Internal server error";
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String DUPLICATE_RESOURCE = "Duplicate resource";
    public static final String VALIDATION_ERROR = "Validation error";

    // Equipment
    public static final String EQUIPMENT_NOT_FOUND = "Equipment not found with ID: ";
    public static final String EQUIPMENT_NOT_FOUND_SERIE = "Equipment not found with serie: ";
    public static final String EQUIPMENT_DUPLICATE_SERIE = "Equipment already exists with serie: ";

    // Area
    public static final String AREA_NOT_FOUND = "Area not found with ID: ";
    public static final String AREA_NOT_FOUND_NAME = "Area not found with name: ";
    public static final String AREA_DUPLICATE = "Area already exists with name: ";
    public static final String AREA_DUPLICATE_UPDATE = "Another area already exists with name: ";

    // Classification
    public static final String CLASSIFICATION_NOT_FOUND = "Classification not found with ID: ";
    public static final String CLASSIFICATION_NOT_FOUND_NAME = "Classification not found with name: ";
    public static final String CLASSIFICATION_DUPLICATE = "Classification already exists with name: ";
    public static final String CLASSIFICATION_DUPLICATE_UPDATE = "Another classification already exists with name: ";

    // Provider
    public static final String PROVIDER_NOT_FOUND = "Provider not found with ID: ";
    public static final String PROVIDER_NOT_FOUND_NAME = "Provider not found with name: ";
    public static final String PROVIDER_DUPLICATE_NAME = "Provider already exists with name: ";
    public static final String PROVIDER_DUPLICATE_EMAIL = "Provider already exists with email: ";
    public static final String PROVIDER_DUPLICATE_UPDATE_NAME = "Another provider already exists with name: ";
    public static final String PROVIDER_DUPLICATE_UPDATE_EMAIL = "Another provider already exists with email: ";

    // States
    public static final String STATE_NOT_FOUND = "State not found with ID: ";
    public static final String STATE_NOT_FOUND_NAME = "State not found with name: ";
    public static final String STATE_DUPLICATE = "State already exists with name: ";
    public static final String STATE_DUPLICATE_UPDATE = "Another state already exists with name: ";

    // Location
    public static final String LOCATION_NOT_FOUND = "Location not found with ID: ";
    public static final String LOCATION_NOT_FOUND_NAME = "Location not found with name: ";
    public static final String LOCATION_DUPLICATE = "Location already exists with name: ";
    public static final String LOCATION_DUPLICATE_UPDATE = "Another location already exists with name: ";
}