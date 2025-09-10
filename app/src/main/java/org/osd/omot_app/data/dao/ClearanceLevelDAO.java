package org.osd.omot_app.data.dao;

import org.osd.omot_app.data.model.ClearanceLevel;

import java.util.List;

public interface ClearanceLevelDAO {
    ClearanceLevel getClearanceByCode(String code);
    List<ClearanceLevel> getAllClearanceLevels();
}