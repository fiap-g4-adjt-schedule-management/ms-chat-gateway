package br.com.fiap.mschatgateway.domain.model.conversation;

public enum ChatStep {

    START,
    MENU,

    SELECT_STATE,
    SELECT_CITY,
    SELECT_NEIGHBORHOOD,
    NO_PHARMACIES,
    SHOW_PHARMACIES,

    ASK_SEARCH_MEDICATION,
    SELECT_MEDICATION_TYPE,
    SELECT_MEDICATION,

    SHOW_RESULT,
    ASK_VIEW_OTHER_PHARMACIES,
    ASK_FEEDBACK,
    ASK_END,

    END
}





