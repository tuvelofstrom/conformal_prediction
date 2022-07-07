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

import static se.redfield.cp.nodes.CompactConformalClassificationNodeModel.PORT_CALIBRATION_TABLE;
import static se.redfield.cp.nodes.CompactConformalClassificationNodeModel.PORT_PREDICTION_TABLE;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import se.redfield.cp.utils.ValidationUtil;

public class CompactRegressionNodeSettings implements CalibratorRegressionSettings, PredictorRegressionSettings {
	private static final String KEY_TARGET_COLUMN_NAME = "targetColumn";
	private static final String KEY_PREDICTION_COLUMN_NAME = "predictionColumn";
	public static final String KEY_ERROR_RATE = "errorRate";

	private static final double DEFAULT_ERROR_RATE = 0.05;

	private final SettingsModelString targetColumn;
	private final SettingsModelString predictionColumn;
	private final RegressionSettings regressionSettings;
	private final KeepColumnsSettings keepColumns;
	private final SettingsModelDoubleBounded errorRate;

	public CompactRegressionNodeSettings() {
		targetColumn = new SettingsModelString(KEY_TARGET_COLUMN_NAME, "");
		predictionColumn = new SettingsModelString(KEY_PREDICTION_COLUMN_NAME, "");
		regressionSettings = new RegressionSettings(PORT_CALIBRATION_TABLE, PORT_PREDICTION_TABLE);
		keepColumns = new KeepColumnsSettings(PORT_PREDICTION_TABLE);
		errorRate = new SettingsModelDoubleBounded(KEY_ERROR_RATE, DEFAULT_ERROR_RATE, 0, 1);
	}


	public SettingsModelString getTargetColumnModel() {
		return targetColumn;
	}

	@Override
	public String getTargetColumnName() {
		return targetColumn.getStringValue();
	}

	public SettingsModelString getPredictionColumnModel() {
		return predictionColumn;
	}

	@Override
	public String getPredictionColumnName() {
		return predictionColumn.getStringValue();
	}

	@Override
	public RegressionSettings getRegressionSettings() {
		return regressionSettings;
	}

	@Override
	public KeepColumnsSettings getKeepColumns() {
		return keepColumns;
	}

	public SettingsModelDoubleBounded getErrorRateModel() {
		return errorRate;
	}

	@Override
	public double getErrorRate() {
		return errorRate.getDoubleValue();
	}

	@Override
	public String getCalibrationAlphaColumnName() {
		return CalibratorRegressionSettings.super.getCalibrationAlphaColumnName();
	}

	@Override
	public String getCalibrationRankColumnName() {
		return CalibratorRegressionSettings.super.getCalibrationRankColumnName();
	}

	public void loadSettingFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		targetColumn.loadSettingsFrom(settings);
		predictionColumn.loadSettingsFrom(settings);
		regressionSettings.loadSettingFrom(settings);
		keepColumns.loadSettingFrom(settings);
		errorRate.loadSettingsFrom(settings);
	}

	public void saveSettingsTo(NodeSettingsWO settings) {
		targetColumn.saveSettingsTo(settings);
		predictionColumn.saveSettingsTo(settings);
		regressionSettings.saveSettingsTo(settings);
		keepColumns.saveSettingsTo(settings);
		errorRate.saveSettingsTo(settings);
	}

	private void validate() throws InvalidSettingsException {
		if (getTargetColumnName().isEmpty()) {
			throw new InvalidSettingsException("Target column is not selected");
		}
		if (getPredictionColumnName().isEmpty()) {
			throw new InvalidSettingsException("Prediction column is not selected");
		}
		regressionSettings.validate();
		keepColumns.validate();
	}

	public void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		CompactRegressionNodeSettings temp = new CompactRegressionNodeSettings();
		temp.loadSettingFrom(settings);
		temp.validate();
	}

	public void validateSettings(DataTableSpec[] inSpecs) throws InvalidSettingsException {
		ValidationUtil.validateDoubleColumn(PORT_CALIBRATION_TABLE, inSpecs, getTargetColumnName(), "Target");
		ValidationUtil.validateDoubleColumn(PORT_CALIBRATION_TABLE, inSpecs, getPredictionColumnName(), "Prediction");
		ValidationUtil.validateDoubleColumn(PORT_PREDICTION_TABLE, inSpecs, getPredictionColumnName(), "Prediction");

		regressionSettings.validateSettings(inSpecs);
		keepColumns.validateSettings(inSpecs);

		validate();
	}

}
