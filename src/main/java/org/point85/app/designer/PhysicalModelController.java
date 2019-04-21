package org.point85.app.designer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.point85.app.AppUtils;
import org.point85.app.EntityNode;
import org.point85.app.FXMLLoaderFactory;
import org.point85.app.ImageManager;
import org.point85.app.Images;
import org.point85.domain.collector.DataSourceType;
import org.point85.domain.i18n.I18n;
import org.point85.domain.persistence.PersistenceService;
import org.point85.domain.plant.Area;
import org.point85.domain.plant.Enterprise;
import org.point85.domain.plant.EntityLevel;
import org.point85.domain.plant.Equipment;
import org.point85.domain.plant.PlantEntity;
import org.point85.domain.plant.ProductionLine;
import org.point85.domain.plant.Site;
import org.point85.domain.plant.WorkCell;
import org.point85.domain.schedule.WorkSchedule;
import org.point85.domain.script.EventResolver;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * Controller for editing and viewing the physical model and its associated
 * entities.
 * 
 * @author Kent Randall
 *
 */
public class PhysicalModelController extends DesignerController {
	// list of edited plant entities
	private final Set<TreeItem<EntityNode>> editedEntityItems = new HashSet<>();

	// entity being edited or viewed
	private TreeItem<EntityNode> selectedEntityItem;

	// equipment material controller
	private EquipmentMaterialController equipmentMaterialController;

	// controller for availability
	private EquipmentResolverController resolverController;

	// work schedule
	private WorkSchedule selectedSchedule;

	// tool bar
	@FXML
	private Button btMaterialEditor;

	@FXML
	private Button btReasonEditor;

	@FXML
	private Button btScheduleEditor;

	@FXML
	private Button btClearSchedule;

	@FXML
	private Button btUomEditor;

	@FXML
	private Button btOpcDaBrowser;

	@FXML
	private Button btOpcUaBrowser;

	@FXML
	private Button btHttpServerEditor;

	@FXML
	private Button btRmqBrokerEditor;

	@FXML
	private Button btJMSBrokerEditor;

	@FXML
	private Button btMQTTBrokerEditor;

	@FXML
	private Button btDatabaseServerEditor;

	@FXML
	private Button btFileShareEditor;

	@FXML
	private Button btCollectorEditor;

	@FXML
	private Button btUomConverter;

	@FXML
	private Button btScriptEditor;

	@FXML
	private Button btAboutDialog;

	// entity section
	@FXML
	private Button btNew;

	@FXML
	private Button btSave;

	@FXML
	private Button btRefresh;

	@FXML
	private Button btDelete;

	// context menu
	@FXML
	private MenuItem miClearSelection;

	@FXML
	private MenuItem miRefreshAll;

	@FXML
	private MenuItem miSaveAll;

	@FXML
	private TreeView<EntityNode> tvEntities;

	@FXML
	private ComboBox<EntityLevel> cbEntityTypes;

	@FXML
	private TextField tfEntityName;

	@FXML
	private TextArea taEntityDescription;

	@FXML
	private TabPane tpEntity;

	// equipment processed material tab
	@FXML
	private AnchorPane apEquipMaterial;

	@FXML
	private Tab tbEquipMaterials;

	// availability tab
	@FXML
	private AnchorPane apAvailability;

	@FXML
	private Tab tbAvailability;

	// class reasons tab
	@FXML
	private AnchorPane apClassReasons;

	// work schedule section
	@FXML
	private Button btWorkSchedule;

	@FXML
	private Label lbSchedule;

	// OEE dashboard
	@FXML
	private Button btDashboard;

	@FXML
	private TextField tfRetention;

	// extract the PlantEntity name from the tree item
	public PlantEntity getSelectedEntity() {
		PlantEntity entity = null;

		if (selectedEntityItem != null) {
			entity = selectedEntityItem.getValue().getPlantEntity();
		}
		return entity;
	}

	// the single root for all entities
	private TreeItem<EntityNode> getRootEntityItem() throws Exception {
		if (tvEntities.getRoot() == null) {
			PlantEntity rootEntity = new PlantEntity();
			rootEntity.setName(PlantEntity.ROOT_ENTITY_NAME);
			tvEntities.setRoot(new TreeItem<>(new EntityNode(rootEntity)));
		}
		return tvEntities.getRoot();
	}

	// initialize app
	void initialize(DesignerApplication app) throws Exception {
		// main app
		setApp(app);

		// images
		setImages();

		// toolbar
		initializeToolbar();

		// add the tree view listener for entity selection
		tvEntities.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
			try {
				onSelectEntity(oldValue, newValue);
			} catch (Exception e) {
				AppUtils.showErrorDialog(e);
			}
		});
		tvEntities.setShowRoot(false);

		// entity types
		cbEntityTypes.getItems().add(EntityLevel.ENTERPRISE);
		cbEntityTypes.getItems().add(EntityLevel.SITE);
		cbEntityTypes.getItems().add(EntityLevel.AREA);
		cbEntityTypes.getItems().add(EntityLevel.PRODUCTION_LINE);
		cbEntityTypes.getItems().add(EntityLevel.WORK_CELL);
		cbEntityTypes.getItems().add(EntityLevel.EQUIPMENT);

		// add the tab pane listener
		tpEntity.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
			try {
				if (newValue.equals(tbEquipMaterials)) {
					onSelectEquipmentMaterial();
				} else if (newValue.equals(tbAvailability)) {
					onSelectEquipmentResolver();
				}
			} catch (Exception e) {
				AppUtils.showErrorDialog(e);
			}
		});

		// disable tabs
		tbEquipMaterials.setDisable(true);
		tbAvailability.setDisable(true);
	}

	// display top-level entities
	void populateTopEntityNodes() throws Exception {
		// fetch the entities
		List<PlantEntity> entities = PersistenceService.instance().fetchTopPlantEntities();

		Collections.sort(entities);

		// add them to the root entity
		ObservableList<TreeItem<EntityNode>> children = getRootEntityItem().getChildren();
		children.clear();

		for (PlantEntity entity : entities) {
			TreeItem<EntityNode> entityItem = new TreeItem<>(new EntityNode(entity));
			children.add(entityItem);
			setEntityGraphic(entityItem);
		}

		// refresh tree view
		getRootEntityItem().setExpanded(true);
		tvEntities.getSelectionModel().clearSelection();
		tvEntities.refresh();
	}

	private void onSelectEntity(TreeItem<EntityNode> oldItem, TreeItem<EntityNode> newItem) throws Exception {
		if (newItem == null) {
			return;
		}

		// new attributes
		selectedEntityItem = newItem;
		PlantEntity selectedEntity = newItem.getValue().getPlantEntity();
		displayAttributes(selectedEntity);

		// show the children too
		Set<PlantEntity> children = selectedEntity.getChildren();
		List<PlantEntity> sortedChildren = new ArrayList<>(children);
		Collections.sort(sortedChildren);

		boolean hasTreeChildren = newItem.getChildren().size() > 0 ? true : false;

		// check to see if the node's children have been previously shown
		if (!hasTreeChildren) {
			newItem.getChildren().clear();
			for (PlantEntity child : children) {
				TreeItem<EntityNode> entityItem = new TreeItem<>(new EntityNode(child));
				newItem.getChildren().add(entityItem);
				this.setEntityGraphic(entityItem);
			}
		}
		newItem.setExpanded(true);

		if (selectedEntity instanceof Equipment) {
			tbAvailability.setDisable(false);
			tbEquipMaterials.setDisable(false);
			btDashboard.setDisable(false);

			onSelectEquipmentMaterial();
		} else {
			tbAvailability.setDisable(true);
			tbEquipMaterials.setDisable(true);
			btDashboard.setDisable(true);
		}
	}

	private void initializeToolbar() throws Exception {
		// toolbar
		I18n i18n = DesignerLocalizer.instance().getLangI18n();
		btMaterialEditor.setGraphic(ImageManager.instance().getImageView(Images.MATERIAL));
		btMaterialEditor.setTooltip(new Tooltip(i18n.getString("material.tt")));

		btReasonEditor.setGraphic(ImageManager.instance().getImageView(Images.REASON));
		btReasonEditor.setTooltip(new Tooltip(i18n.getString("reason.tt")));

		btScheduleEditor.setGraphic(ImageManager.instance().getImageView(Images.SCHEDULE));
		btScheduleEditor.setTooltip(new Tooltip(i18n.getString("schedule.tt")));

		btUomEditor.setGraphic(ImageManager.instance().getImageView(Images.UOM));
		btUomEditor.setTooltip(new Tooltip(i18n.getString("uom.tt")));

		btOpcDaBrowser.setGraphic(ImageManager.instance().getImageView(Images.OPC_DA));
		btOpcDaBrowser.setTooltip(new Tooltip(i18n.getString("opc.da.tt")));

		btOpcUaBrowser.setGraphic(ImageManager.instance().getImageView(Images.OPC_UA));
		btOpcUaBrowser.setTooltip(new Tooltip(i18n.getString("opc.ua.tt")));

		btHttpServerEditor.setGraphic(ImageManager.instance().getImageView(Images.HTTP));
		btHttpServerEditor.setTooltip(new Tooltip(i18n.getString("http.tt")));

		btRmqBrokerEditor.setGraphic(ImageManager.instance().getImageView(Images.RMQ));
		btRmqBrokerEditor.setTooltip(new Tooltip(i18n.getString("rmq.tt")));

		btJMSBrokerEditor.setGraphic(ImageManager.instance().getImageView(Images.JMS));
		btJMSBrokerEditor.setTooltip(new Tooltip(i18n.getString("jms.tt")));

		btMQTTBrokerEditor.setGraphic(ImageManager.instance().getImageView(Images.MQTT));
		btMQTTBrokerEditor.setTooltip(new Tooltip(i18n.getString("mqtt.tt")));

		btDatabaseServerEditor.setGraphic(ImageManager.instance().getImageView(Images.DB));
		btDatabaseServerEditor.setTooltip(new Tooltip(i18n.getString("db.tt")));

		btFileShareEditor.setGraphic(ImageManager.instance().getImageView(Images.FILE));
		btFileShareEditor.setTooltip(new Tooltip(i18n.getString("file.tt")));

		btCollectorEditor.setGraphic(ImageManager.instance().getImageView(Images.COLLECTOR));
		btCollectorEditor.setTooltip(new Tooltip(i18n.getString("collector.tt")));

		btUomConverter.setGraphic(ImageManager.instance().getImageView(Images.CONVERT));
		btUomConverter.setTooltip(new Tooltip(i18n.getString("uom.converter.tt")));

		btScriptEditor.setGraphic(ImageManager.instance().getImageView(Images.SCRIPT));
		btScriptEditor.setTooltip(new Tooltip(i18n.getString("script.tt")));

		btAboutDialog.setGraphic(ImageManager.instance().getImageView(Images.ABOUT));
		btAboutDialog.setTooltip(new Tooltip(i18n.getString("about.tt")));
	}

	// images for editor buttons
	protected void setImages() throws Exception {
		// new entity
		btNew.setGraphic(ImageManager.instance().getImageView(Images.NEW));
		btNew.setContentDisplay(ContentDisplay.RIGHT);

		// save entity
		btSave.setGraphic(ImageManager.instance().getImageView(Images.SAVE));
		btSave.setContentDisplay(ContentDisplay.RIGHT);

		// save all
		btRefresh.setGraphic(ImageManager.instance().getImageView(Images.REFRESH));
		btRefresh.setContentDisplay(ContentDisplay.RIGHT);

		// delete entity
		btDelete.setGraphic(ImageManager.instance().getImageView(Images.DELETE));
		btDelete.setContentDisplay(ContentDisplay.RIGHT);

		// work schedule
		btWorkSchedule.setGraphic(ImageManager.instance().getImageView(Images.SCHEDULE));
		btWorkSchedule.setContentDisplay(ContentDisplay.LEFT);

		// clear work schedule
		btClearSchedule.setGraphic(ImageManager.instance().getImageView(Images.CLEAR));
		btClearSchedule.setContentDisplay(ContentDisplay.LEFT);

		// dashboard
		btDashboard.setGraphic(ImageManager.instance().getImageView(Images.DASHBOARD));
		btDashboard.setContentDisplay(ContentDisplay.RIGHT);

		// context menu
		miSaveAll.setGraphic(ImageManager.instance().getImageView(Images.SAVE_ALL));
		miRefreshAll.setGraphic(ImageManager.instance().getImageView(Images.REFRESH_ALL));
		miClearSelection.setGraphic(ImageManager.instance().getImageView(Images.CLEAR));
	}

	@FXML
	private void onShowCollectorEditor() {
		try {
			this.getApp().showCollectorEditor();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowHttpServerEditor() {
		try {
			this.getApp().showHttpServerEditor();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowRmqBrokerEditor() {
		try {
			this.getApp().showMQBrokerEditor(DataSourceType.MESSAGING);
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowJMSBrokerEditor() {
		try {
			this.getApp().showMQBrokerEditor(DataSourceType.JMS);
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowMQTTBrokerEditor() {
		try {
			this.getApp().showMQBrokerEditor(DataSourceType.MQTT);
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowDatabaseServerEditor() {
		try {
			this.getApp().showDatabaseServerEditor();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowFileShareEditor() {
		try {
			this.getApp().showFileShareEditor();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowMaterialEditor() {
		try {
			this.getApp().showMaterialEditor();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowReasonEditor() {
		try {
			this.getApp().showReasonEditor();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowScheduleEditor() {
		try {
			this.getApp().showScheduleEditor();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowScriptEditor() {
		try {
			EventResolver eventResolver = new EventResolver();

			getApp().showScriptEditor(eventResolver);
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowUomEditor() {
		try {
			this.getApp().showUomEditor();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowOpcDaBrowser() {
		try {
			this.getApp().showOpcDaDataSourceBrowser();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowOpcUaBrowser() {
		try {
			this.getApp().showOpcUaDataSourceBrowser();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowUomConverter() {
		try {
			this.getApp().showUomConverter();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onShowAboutDialog() {
		try {
			this.getApp().showAboutDialog();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onNewEntity() {
		try {
			selectedSchedule = null;

			// main editing
			tfEntityName.clear();
			taEntityDescription.setText(null);
			cbEntityTypes.getSelectionModel().clearSelection();
			cbEntityTypes.getSelectionModel().select(null);
			cbEntityTypes.requestFocus();
			lbSchedule.setText(null);
			lbSchedule.setUserData(null);
			tfRetention.clear();

			// no entity item selection
			selectedEntityItem = null;

			// equipment materials
			if (equipmentMaterialController != null) {
				equipmentMaterialController.clear();
			}

			// data collection
			getResolverController().clear();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	private boolean createEntity() {
		try {
			// chosen level of child
			EntityLevel childLevel = this.cbEntityTypes.getSelectionModel().getSelectedItem();

			// parent entity
			TreeItem<EntityNode> parentItem = this.tvEntities.getSelectionModel().getSelectedItem();

			if (parentItem == null) {
				// confirm
				ButtonType type = AppUtils.showConfirmationDialog(
						DesignerLocalizer.instance().getLangString("add.entity", childLevel.toString()));

				if (type.equals(ButtonType.CANCEL)) {
					return false;
				}

				// add to all entities
				parentItem = tvEntities.getRoot();

				if (parentItem == null) {
					throw new Exception(DesignerLocalizer.instance().getErrorString("create.entity"));
				}
			} else {
				// confirm
				ButtonType type = AppUtils.showConfirmationDialog(DesignerLocalizer.instance()
						.getLangString("add.entity.parent", parentItem.getValue().getPlantEntity().getName()));

				if (type.equals(ButtonType.CANCEL)) {
					return false;
				}
			}

			PlantEntity parentEntity = parentItem.getValue().getPlantEntity();
			EntityLevel parentLevel = parentEntity.getLevel();

			// create and add child entity
			PlantEntity newEntity = null;

			if (parentLevel != null) {
				switch (parentLevel) {
				case ENTERPRISE:
					childLevel = EntityLevel.SITE;
					break;
				case SITE:
					childLevel = EntityLevel.AREA;
					break;
				case AREA:
					childLevel = EntityLevel.PRODUCTION_LINE;
					break;
				case PRODUCTION_LINE:
					childLevel = EntityLevel.WORK_CELL;
					break;
				case WORK_CELL:
					childLevel = EntityLevel.EQUIPMENT;
					break;
				default:
					return false;
				}
			}

			if (childLevel == null) {
				throw new Exception(DesignerLocalizer.instance().getErrorString("no.level"));
			}

			switch (childLevel) {
			case ENTERPRISE:
				newEntity = new Enterprise();
				break;
			case SITE:
				newEntity = new Site();
				break;
			case AREA:
				newEntity = new Area();
				break;
			case PRODUCTION_LINE:
				newEntity = new ProductionLine();
				break;
			case WORK_CELL:
				newEntity = new WorkCell();
				break;
			case EQUIPMENT:
				newEntity = new Equipment();
				break;
			default:
				return false;
			}

			// set the new entity's attributes
			selectedEntityItem = new TreeItem<>(new EntityNode(newEntity));
			setAttributes(selectedEntityItem);

			// add new child entity if not a top level
			if (!parentEntity.getName().equals(PlantEntity.ROOT_ENTITY_NAME)) {
				parentEntity.addChild(newEntity);
				parentItem.setGraphic(ImageManager.instance().getImageView(Images.CHANGED));
			}

			// add to tree view
			parentItem.getChildren().add(selectedEntityItem);
			selectedEntityItem.setGraphic(ImageManager.instance().getImageView(Images.CHANGED));
			addEditedPlantEntity(selectedEntityItem);

			parentItem.setExpanded(true);
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
		return true;
	}

	private void setEntityGraphic(TreeItem<EntityNode> item) throws Exception {
		ImageView nodeView = null;
		PlantEntity entity = item.getValue().getPlantEntity();
		EntityLevel level = entity.getLevel();

		if (level == null) {
			return;
		}

		switch (level) {
		case AREA:
			nodeView = ImageManager.instance().getImageView(Images.AREA);
			break;
		case ENTERPRISE:
			nodeView = ImageManager.instance().getImageView(Images.ENTERPRISE);
			break;
		case EQUIPMENT:
			nodeView = ImageManager.instance().getImageView(Images.EQUIPMENT);
			break;

		case PRODUCTION_LINE:
			nodeView = ImageManager.instance().getImageView(Images.LINE);
			break;

		case SITE:
			nodeView = ImageManager.instance().getImageView(Images.SITE);
			break;

		case WORK_CELL:
			nodeView = ImageManager.instance().getImageView(Images.CELL);
			break;

		default:
			break;
		}

		item.setGraphic(nodeView);
	}

	@FXML
	private void onClearSelection() {
		tvEntities.getSelectionModel().clearSelection();
		selectedEntityItem = null;
	}

	@FXML
	private void onRefreshEntity() {
		try {
			PlantEntity selectedEntity = getSelectedEntity();
			if (selectedEntity == null) {
				onNewEntity();
				return;
			}

			PlantEntity refreshed = PersistenceService.instance().fetchPlantEntityByName(selectedEntity.getName());
			selectedEntityItem.getValue().setPlantEntity(refreshed);
			selectedEntity = refreshed;
			displayAttributes(selectedEntity);

			removeEditedPlantEntity(selectedEntityItem);

			tvEntities.refresh();

		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onRefreshAllEntities() {
		try {
			populateTopEntityNodes();
			onNewEntity();
		} catch (Exception e) {
			AppUtils.showErrorDialog(DesignerLocalizer.instance().getErrorString("fetch.entities", e.getMessage()));
		}
	}

	// Delete button clicked
	@FXML
	private void onDeleteEntity() {
		PlantEntity selectedEntity = getSelectedEntity();

		if (selectedEntity == null) {
			AppUtils.showErrorDialog(DesignerLocalizer.instance().getErrorString("no.entity"));
			return;
		}

		// confirm
		ButtonType type = AppUtils.showConfirmationDialog(
				DesignerLocalizer.instance().getLangString("confirm.delete", selectedEntity.getName()));

		if (type.equals(ButtonType.CANCEL)) {
			return;
		}

		try {
			// delete from db if previously saved
			PlantEntity parentEntity = selectedEntity.getParent();
			if (parentEntity != null) {
				// remove from parent with orphan removal
				parentEntity.removeChild(selectedEntity);
				PersistenceService.instance().save(parentEntity);
			} else {
				// cascade delete
				PersistenceService.instance().delete(selectedEntity);
			}

			// remove this entity from the tree
			TreeItem<EntityNode> selectedEntityItem = tvEntities.getSelectionModel().getSelectedItem();
			TreeItem<EntityNode> parentNode = selectedEntityItem.getParent();
			parentNode.getChildren().remove(selectedEntityItem);
			tvEntities.getSelectionModel().clearSelection();

			tvEntities.refresh();
			parentNode.setExpanded(true);

			onNewEntity();

		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	private void fillPersistenceContext(PlantEntity entity) {
		// bring children into the persistence context
		for (PlantEntity child : entity.getChildren()) {
			fillPersistenceContext(child);
		}
	}

	// Save button clicked
	@FXML
	private void onSaveEntity() {
		try {
			if (selectedEntityItem == null) {
				// create
				if (!createEntity()) {
					return;
				}
			} else {
				// update
				setAttributes(selectedEntityItem);
			}

			// save modified entity
			PlantEntity entity = getSelectedEntity();

			// bring children into persistence context
			fillPersistenceContext(entity);

			PlantEntity saved = (PlantEntity) PersistenceService.instance().save(entity);

			selectedEntityItem.getValue().setPlantEntity(saved);
			setEntityGraphic(selectedEntityItem);

			if (selectedEntityItem.getParent() != null) {
				setEntityGraphic(selectedEntityItem.getParent());
			} else {
				// is a top-level entity
				getRootEntityItem().getChildren().add(selectedEntityItem);
			}

			editedEntityItems.remove(selectedEntityItem);

			// re-display attributes
			onRefreshEntity();

		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	@FXML
	private void onSaveAllEntities() {
		try {
			// current entity could have been edited
			setAttributes(selectedEntityItem);

			// save all modified entities
			for (TreeItem<EntityNode> editedEntityItem : editedEntityItems) {
				EntityNode node = editedEntityItem.getValue();
				PlantEntity entity = node.getPlantEntity();
				PlantEntity saved = (PlantEntity) PersistenceService.instance().save(entity);
				node.setPlantEntity(saved);
				setEntityGraphic(editedEntityItem);
			}

			editedEntityItems.clear();

			tvEntities.refresh();

		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	// show the entity attributes
	private void displayAttributes(PlantEntity entity) throws Exception {
		if (entity == null) {
			return;
		}

		// name
		this.tfEntityName.setText(entity.getName());

		// description
		this.taEntityDescription.setText(entity.getDescription());

		// level
		cbEntityTypes.getSelectionModel().select(entity.getLevel());

		// work schedule
		if (entity.getWorkSchedule() != null) {
			this.lbSchedule.setText(entity.getWorkSchedule().getName());
			this.lbSchedule.setUserData(entity.getWorkSchedule());
		} else {
			this.lbSchedule.setText(null);
			this.lbSchedule.setUserData(null);
		}

		// retention period
		if (entity.getRetentionDuration() != null) {
			long days = entity.getRetentionDuration().toDays();
			this.tfRetention.setText(String.valueOf(days));
		} else {
			this.tfRetention.setText(null);
		}

		if (entity instanceof Equipment) {
			if (equipmentMaterialController != null) {
				equipmentMaterialController.showMaterial((Equipment) entity);
			}

			if (resolverController != null) {
				resolverController.showResolvers((Equipment) entity);
			}
		}
	}

	private void setAttributes(TreeItem<EntityNode> entityItem) throws Exception {
		PlantEntity entity = entityItem.getValue().getPlantEntity();

		// name
		String name = tfEntityName.getText().trim();

		if (name.length() == 0) {
			throw new Exception(DesignerLocalizer.instance().getErrorString("no.name"));
		}
		entity.setName(name);

		// description
		String description = taEntityDescription.getText();
		entity.setDescription(description);

		// retention period
		String period = tfRetention.getText();

		if (period != null && period.length() > 0) {
			Long days = AppUtils.stringToLong(period.trim());

			if (days < 0) {
				throw new Exception(DesignerLocalizer.instance().getErrorString("invalid.retention"));
			}
			entity.setRetentionDuration(Duration.ofDays(days));
		}

		// work schedule
		WorkSchedule schedule = (WorkSchedule) lbSchedule.getUserData();
		entity.setWorkSchedule(schedule);

		addEditedPlantEntity(entityItem);
	}

	private void addEditedPlantEntity(TreeItem<EntityNode> item) throws Exception {
		if (item != null && !editedEntityItems.contains(item)) {
			item.setGraphic(ImageManager.instance().getImageView(Images.CHANGED));
			editedEntityItems.add(item);
		}
	}

	private void removeEditedPlantEntity(TreeItem<EntityNode> item) throws Exception {
		if (item != null && editedEntityItems.contains(item)) {
			setEntityGraphic(item);
			editedEntityItems.remove(item);
		}
	}

	void markSelectedPlantEntity() throws Exception {
		this.addEditedPlantEntity(selectedEntityItem);
	}

	// find work schedule
	@FXML
	private void onFindWorkSchedule() {
		try {
			// get the work schedule from the dialog
			selectedSchedule = getApp().showScheduleEditor();

			if (selectedSchedule == null) {
				return;
			}

			// work schedule
			if (getSelectedEntity() != null) {
				getSelectedEntity().setWorkSchedule(selectedSchedule);
				addEditedPlantEntity(selectedEntityItem);
			}

			// add schedule to text field
			lbSchedule.setText(selectedSchedule.getName());
			lbSchedule.setUserData(selectedSchedule);

		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
		}
	}

	private void onSelectEquipmentMaterial() throws Exception {
		if (equipmentMaterialController == null) {
			// Load the fxml file and create the anchor pane
			FXMLLoader loader = FXMLLoaderFactory.equipmentMaterialLoader();
			AnchorPane pane = (AnchorPane) loader.getRoot();
			tbEquipMaterials.setContent(pane);

			equipmentMaterialController = loader.getController();

			equipmentMaterialController.initialize(getApp());
		}

		// show materials
		if (getSelectedEntity() instanceof Equipment) {
			equipmentMaterialController.showMaterial((Equipment) getSelectedEntity());
		} else {
			equipmentMaterialController.clearEditor();
		}
	}

	private EquipmentResolverController getResolverController() throws Exception {
		if (resolverController == null) {
			// Load the fxml file and create the anchor pane
			FXMLLoader loader = FXMLLoaderFactory.equipmentResolverLoader();
			AnchorPane pane = (AnchorPane) loader.getRoot();
			tbAvailability.setContent(pane);

			resolverController = loader.getController();
			resolverController.initialize(getApp());
		}
		return resolverController;
	}

	private void onSelectEquipmentResolver() throws Exception {

		// show entity resolvers
		if (getSelectedEntity() instanceof Equipment) {
			getResolverController().showResolvers((Equipment) getSelectedEntity());
		} else {
			getResolverController().clearEditor();
		}
	}

	@FXML
	private void onShowDashboard() {
		try {
			getApp().showDashboard();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
			return;
		}
	}

	@FXML
	private void onClearSchedule() {
		try {
			if (getSelectedEntity() == null || getSelectedEntity().getWorkSchedule() == null) {
				return;
			}

			getSelectedEntity().setWorkSchedule(null);
			selectedSchedule = null;
			lbSchedule.setText(null);
			lbSchedule.setUserData(null);
			markSelectedPlantEntity();
		} catch (Exception e) {
			AppUtils.showErrorDialog(e);
			return;
		}
	}
}
