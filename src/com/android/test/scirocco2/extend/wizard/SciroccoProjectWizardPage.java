package com.android.test.scirocco2.extend.wizard;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WorkingSetGroup;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea;
import org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea.IErrorMessageReporter;

import com.android.test.scirocco2.Activator;
import com.android.test.scirocco2.preference.Scirocco2PreferenceInitializer;

public class SciroccoProjectWizardPage extends WizardPage {

    private static final String MASSAGE_APK_EMPTY = "Target app package information must be specified";
    
    private static final String MASSAGE_SDK_EMPTY = "Android SDK path must be specified";
    
    private static final String LABEL_APK = "Target app package:";
    
    private static final String LABEL_ADB = "Android sdk path:";
    
    // initial value stores
    private String initialProjectFieldValue;
    
    // widgets
    Text projectNameField;
    
    Text apkPackageField;
    
    DirectoryFieldEditor adbPathField;
    
    private Listener nameModifyListener;

    private ProjectContentsLocationArea locationArea;

    private WorkingSetGroup workingSetGroup;

    // constants
    private static final int SIZING_TEXT_FIELD_WIDTH = 250;

    public SciroccoProjectWizardPage(String pageName) {
        super(pageName);
        setPageComplete(false);
    }

    public SciroccoProjectWizardPage(String pageName, IStructuredSelection selection, String[] workingSetTypes) {
        this(pageName);
    }

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        initializeDialogUnits(parent);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IIDEHelpContextIds.NEW_PROJECT_WIZARD_PAGE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        createProjectNameGroup(composite);
        // Scale the button based on the rest of the dialog
        setButtonLayoutData(locationArea.getBrowseButton());
        setPageComplete(validatePage());
        // Show description on opening
        setErrorMessage(null);
        setMessage(null);
        setControl(composite);
        Dialog.applyDialogFont(composite);
    }
    
    public WorkingSetGroup createWorkingSetGroup(Composite composite, IStructuredSelection selection, String[] supportedWorkingSetTypes) {
        if (workingSetGroup != null) {
            return workingSetGroup;
        }
        workingSetGroup = new WorkingSetGroup(composite, selection, supportedWorkingSetTypes);
        return workingSetGroup;
    }
    
    private IErrorMessageReporter getErrorReporter() {
        return new IErrorMessageReporter() {
            @Override
            public void reportError(String paramString, boolean paramBoolean) {
                setErrorMessage(paramString);
                boolean valid = paramString == null;
                if(valid) {
                    valid = validatePage();
                }
                setPageComplete(valid);
            }
        };
    }

    private final void createProjectNameGroup(Composite parent) {
        // project specification group
        Composite projectGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        projectGroup.setLayout(layout);
        projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Label projectLabel = new Label(projectGroup, SWT.NONE);
        projectLabel.setText(IDEWorkbenchMessages.WizardNewProjectCreationPage_nameLabel);
        projectLabel.setFont(parent.getFont());
        projectNameField = new Text(projectGroup, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        projectNameField.setLayoutData(data);
        projectNameField.setFont(parent.getFont());
        if (initialProjectFieldValue != null) {
            projectNameField.setText(initialProjectFieldValue);
        }
        locationArea = new ProjectContentsLocationArea(getErrorReporter(), parent);
        if(initialProjectFieldValue != null) {
            locationArea.updateProjectName(initialProjectFieldValue);
        }
        projectNameField.addListener(SWT.Modify, getNameModifyListener());
        // apk package specification group
        Composite apkPackageGroup = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        apkPackageGroup.setLayout(layout);
        apkPackageGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Label apkPackageLabel = new Label(apkPackageGroup, SWT.NONE);
        apkPackageLabel.setText(LABEL_APK);
        apkPackageLabel.setFont(parent.getFont());
        apkPackageField = new Text(apkPackageGroup, SWT.BORDER);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.widthHint = SIZING_TEXT_FIELD_WIDTH;
        apkPackageField.setLayoutData(gridData);
        apkPackageField.setFont(parent.getFont());
        apkPackageField.addListener(SWT.Modify, getNameModifyListener());
        // adb path specification group
        Composite adbPathGroup = new Composite(parent, SWT.NONE);
        GridLayout gLayout = new GridLayout();
        adbPathGroup.setLayout(gLayout);
        adbPathGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        adbPathField =  new DirectoryFieldEditor(
        		Scirocco2PreferenceInitializer.ANDROID_SDK_LOCATION,
        		LABEL_ADB,
        		adbPathGroup);
        adbPathField.setPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
                setPageComplete(!adbPathField.getStringValue().equals(""));
            }
        });
        String sdkLocation = store.getString(Scirocco2PreferenceInitializer.ANDROID_SDK_LOCATION);
        if ( sdkLocation == null || sdkLocation.length() == 0 ) {
        	//Preferences prefs = InstanceScope.INSTANCE.getNode("com.android.ide.eclipse.adt");
        	//sdkLocation = prefs.get("com.android.ide.eclipse.adt.sdk", "");
            sdkLocation = "";
        }
        adbPathField.setStringValue(sdkLocation);
    }

    public IPath getLocationPath() {
        return new Path(locationArea.getProjectLocation());
    }
    
    public URI getLocationURI() {
        return locationArea.getProjectLocationURI();
    }

    public IProject getProjectHandle() {
        return ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName());
    }

    public String getProjectName() {
        if (projectNameField == null) {
            return initialProjectFieldValue;
        }
        return getProjectNameFieldValue();
    }

    private String getProjectNameFieldValue() {
        if (projectNameField == null) {
            return "";
        }
        return projectNameField.getText().trim();
    }

    private String getApkPackageFieldValue() {
        if (apkPackageField == null) {
            return "";
        }
        return apkPackageField.getText().trim();
    }
    
    private String getAdbPathFieldValue() {
        if (adbPathField == null) {
            return "";
        }
        return adbPathField.getStringValue().trim();
    }
    
    
    public void setInitialProjectName(String name) {
        if (name == null) {
            initialProjectFieldValue = null;
        } else {
            initialProjectFieldValue = name.trim();
            if(locationArea != null) {
                locationArea.updateProjectName(name.trim());
            }
        }
    }

    void setLocationForSelection() {
        locationArea.updateProjectName(getProjectNameFieldValue());
    }

    protected boolean validatePage() {
        IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();
        String projectFieldContents = getProjectNameFieldValue();
        String apkPackageFieldContents = getApkPackageFieldValue();
        String adbPathFieldContents = getAdbPathFieldValue();
        if (projectFieldContents.equals("")) { 
            setErrorMessage(null);
            setMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectNameEmpty);
            return false;
        }
        if (apkPackageFieldContents.equals("")) { 
            setErrorMessage(null);
            setMessage(MASSAGE_APK_EMPTY);
            return false;
        }
        if (adbPathFieldContents.equals("")) { 
            setErrorMessage(null);
            setMessage(MASSAGE_SDK_EMPTY);
            return false;
        }
        IStatus nameStatus = workspace.validateName(projectFieldContents, IResource.PROJECT);
        if (!nameStatus.isOK()) {
            setErrorMessage(nameStatus.getMessage());
            return false;
        }
        IProject handle = getProjectHandle();
        if (handle.exists()) {
            setErrorMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectExistsMessage);
            return false;
        }
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectNameFieldValue());
        locationArea.setExistingProject(project);
        String validLocationMessage = locationArea.checkValidLocation();
        if (validLocationMessage != null) { 
            setErrorMessage(validLocationMessage);
            return false;
        }
        setErrorMessage(null);
        setMessage(null);
        return true;
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            projectNameField.setFocus();
        }
    }

    public boolean useDefaults() {
        return locationArea.isDefault();
    }

    public IWorkingSet[] getSelectedWorkingSets() {
        return workingSetGroup == null ? new IWorkingSet[0] : workingSetGroup.getSelectedWorkingSets();
    }
    
    private Listener getNameModifyListener() {
        if (nameModifyListener == null) {
            nameModifyListener = new Listener() {
                public void handleEvent(Event e) {
                    setLocationForSelection();
                    boolean valid = validatePage();
                    setPageComplete(valid);
                }
            };
        }
        return nameModifyListener;
    }
}