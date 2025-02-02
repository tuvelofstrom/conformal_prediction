/*
 * Copyright (c) 2020 Redfield AB.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, Version 3, as
 * published by the Free Software Foundation.
 *  
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */
package se.redfield.cp.settings;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import se.redfield.cp.utils.PortDef;

/**
 * Columns retention settings.
 * 
 * @author Alexander Bondaletov
 *
 */
public class KeepColumnsSettings {
	private static final String KEY_KEEP_ALL_COLUMNS = "keepAllColumns";
	private static final String KEY_KEEP_ID_COLUMN = "keepIdColumn";
	private static final String KEY_ID_COLUMN = "idColumn";

	private final PortDef table;

	protected final SettingsModelBoolean keepAllColumns;
	protected final SettingsModelBoolean keepIdColumn;
	protected final SettingsModelString idColumn;

	/**
	 * @param table The table settings are applied to.
	 */
	public KeepColumnsSettings(PortDef table) {
		this.table = table;
		keepAllColumns = new SettingsModelBoolean(KEY_KEEP_ALL_COLUMNS, true);
		keepIdColumn = new SettingsModelBoolean(KEY_KEEP_ID_COLUMN, false);
		idColumn = new SettingsModelString(KEY_ID_COLUMN, "");

		keepAllColumns.addChangeListener(e -> {
			keepIdColumn.setEnabled(!keepAllColumns.getBooleanValue());
			if (!keepIdColumn.isEnabled()) {
				keepIdColumn.setBooleanValue(false);
			}
		});
		keepIdColumn.addChangeListener(e -> idColumn.setEnabled(keepIdColumn.getBooleanValue()));

		keepIdColumn.setEnabled(!keepAllColumns.getBooleanValue());
		idColumn.setEnabled(keepIdColumn.getBooleanValue());
	}

	/**
	 * @return The keepAllColumns settings model.
	 */
	public SettingsModelBoolean getKeepAllColumnsModel() {
		return keepAllColumns;
	}

	/**
	 * @return Whether to retain all columns from the input table.
	 */
	public boolean getKeepAllColumns() {
		return keepAllColumns.getBooleanValue();
	}

	/**
	 * @return The keepIdColumn model.
	 */
	public SettingsModelBoolean getKeepIdColumnModel() {
		return keepIdColumn;
	}

	/**
	 * @return Whether to retain an id column from the input table.
	 */
	public boolean getKeepIdColumn() {
		return keepIdColumn.getBooleanValue();
	}

	/**
	 * @return The idColumn settings model.
	 */
	public SettingsModelString getIdColumnModel() {
		return idColumn;
	}

	/**
	 * @return The id column name.
	 */
	public String getIdColumn() {
		return idColumn.getStringValue();
	}

	/**
	 * Loads settings from the provided {@link NodeSettingsRO}
	 * 
	 * @param settings
	 * @throws InvalidSettingsException
	 */
	public void loadSettingFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		keepAllColumns.loadSettingsFrom(settings);
		keepIdColumn.loadSettingsFrom(settings);
		idColumn.loadSettingsFrom(settings);
	}

	/**
	 * Saves current settings into the given {@link NodeSettingsWO}.
	 * 
	 * @param settings
	 */
	public void saveSettingsTo(NodeSettingsWO settings) {
		keepAllColumns.saveSettingsTo(settings);
		keepIdColumn.saveSettingsTo(settings);
		idColumn.saveSettingsTo(settings);
	}

	/**
	 * Validates internal consistency of the current settings
	 * 
	 * @throws InvalidSettingsException
	 */
	public void validate() throws InvalidSettingsException {
		if (!getKeepAllColumns() && getKeepIdColumn() && getIdColumn().isEmpty()) {
			throw new InvalidSettingsException("Id column is not selected");
		}
	}

	/**
	 * Validates the settings against input table spec.
	 * 
	 * @param inSpecs Input specs
	 * @throws InvalidSettingsException
	 */
	public void validateSettings(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		validate();

		if (!getKeepAllColumns() && getKeepIdColumn() && !inSpecs[table.getIdx()].containsName(getIdColumn())) {
			throw new InvalidSettingsException(table.getName() + ":Id column not found: " + getIdColumn());
		}
	}
}
