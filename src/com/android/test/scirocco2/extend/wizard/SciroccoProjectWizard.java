package com.android.test.scirocco2.extend.wizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.dialogs.WizardNewProjectReferencePage;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.android.test.scirocco2.Activator;
import com.android.test.scirocco2.preference.Scirocco2PreferenceInitializer;
import com.android.test.scirocco2.service.adb.AdbService;
import com.android.test.scirocco2.util.FileUtil;
import com.android.test.scirocco2.util.PathUtil;

public class SciroccoProjectWizard extends BasicNewResourceWizard implements IExecutableExtension {
    
    private static String WINDOW_PROBLEMS_TITLE = ResourceMessages.NewProject_errorOpeningWindow;
    
    private static final String SEPARATOR = System.getProperty("file.separator");
    
    private static final String PROJECT_PROPERTIES = "scirocco.properties";
    
    private static final String TARGET_APK_KEY = "target_apk";
    
    private static final String ADB_PATH_KEY = "adb_path";
    
    private static final String SRC_DIR = "src";
    
    private static final String BIN_DIR = "bin";
    
    private static final String LIB_DIR = "lib";
    
    private SciroccoProjectWizardPage mainPage;

    private WizardNewProjectReferencePage referencePage;

    private IProject newProject;

    private IConfigurationElement configElement;
    
    private String apkPackage;

    public SciroccoProjectWizard() {
        IDialogSettings workbenchSettings = IDEWorkbenchPlugin.getDefault().getDialogSettings();
        IDialogSettings section = workbenchSettings
                .getSection("BasicNewProjectResourceWizard");
        if (section == null) {
            section = workbenchSettings.addNewSection("BasicNewProjectResourceWizard");
        }
        setDialogSettings(section);
    }

    public void addPages() {
        super.addPages();
        mainPage = new SciroccoProjectWizardPage("basicNewProjectPage") {
            public void createControl(Composite parent) {
                super.createControl(parent);
                createWorkingSetGroup((Composite) getControl(), getSelection(), new String[] { "org.eclipse.ui.resourceWorkingSetPage" });
            }
        };
        mainPage.setTitle(ResourceMessages.NewProject_title);
        mainPage.setDescription(ResourceMessages.NewProject_description);
        this.addPage(mainPage);
        // only add page if there are already projects in the workspace
        if (ResourcesPlugin.getWorkspace().getRoot().getProjects().length > 0) {
            referencePage = new WizardNewProjectReferencePage("basicReferenceProjectPage");
            referencePage.setTitle(ResourceMessages.NewProject_referenceTitle);
            referencePage.setDescription(ResourceMessages.NewProject_referenceDescription);
            this.addPage(referencePage);
        }
    }

    private void createNewProject() {
        if (newProject != null) {
            return;
        }
        // create the new project operation
        apkPackage = mainPage.apkPackageField.getText();
        final IProject project = mainPage.getProjectHandle();
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    project.create(monitor);
                    project.open(monitor);
                    IProjectDescription description = project.getDescription();
                    String[] natures = description.getNatureIds();
                    String[] newNatures = new String[natures.length + 1];
                    System.arraycopy(natures, 0, newNatures, 0, natures.length);
                    newNatures[natures.length] = JavaCore.NATURE_ID;
                    description.setNatureIds(newNatures);
                    project.setDescription(description, monitor);
                    IJavaProject javaProject = JavaCore.create(project);
                    // set basic classpath
                    Set<IClasspathEntry> entries = new HashSet<IClasspathEntry>();
                    IPath sourcePath = javaProject.getPath().append(SRC_DIR);
                    IFolder sourceDir = project.getFolder(new Path(SRC_DIR));
                    if (!sourceDir.exists()) {
                        sourceDir.create(false, true, null);
                    }
                    IPath outputPath = javaProject.getPath().append(BIN_DIR);
                    IFolder outputDir = project.getFolder(new Path(BIN_DIR));
                    if (!outputDir.exists()) {
                        outputDir.create(false, true, null);
                    }
                    IClasspathEntry srcEntry = JavaCore.newSourceEntry(sourcePath, new IPath[] {}, outputPath);
                    IClasspathEntry junitEntry = JavaCore.newContainerEntry(new Path("org.eclipse.jdt.junit.JUNIT_CONTAINER/3"));
                    entries.add(JavaRuntime.getDefaultJREContainerEntry());
                    entries.add(junitEntry);
                    entries.add(srcEntry);
                    // set extends classpath
                    final String destLibPath = javaProject.getResource().getLocationURI().getPath() + SEPARATOR + LIB_DIR;
                    FileUtil.makeDir(destLibPath);
                    File dir = new File(PathUtil.getLibDirPath());
                    String[] jarList = dir.list(FileUtil.getFileExtensionFilter(".jar"));
                    for (final String jar : jarList) {
                        FileUtil.jarCopy(PathUtil.getLibDirPath() + SEPARATOR + jar, destLibPath + SEPARATOR + jar);
                        IClasspathEntry libEntry = JavaCore.newLibraryEntry(new Path(SEPARATOR + project.getName() + SEPARATOR + LIB_DIR + SEPARATOR + jar) , null, null);
                        entries.add(libEntry);
                    }
                    // apply classpath
                    javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), monitor);
                    // save properties
                    String propertiesPath = javaProject.getResource().getLocationURI().getPath() + SEPARATOR + PROJECT_PROPERTIES;
                    Map<String, String> properties = new HashMap<String, String>();
                    String adbPath = AdbService.getAdbPath().trim();
                    // check windows path
                    if (adbPath.indexOf(':') > 0) {
                        adbPath = adbPath.replaceAll(SEPARATOR + SEPARATOR, SEPARATOR + SEPARATOR + SEPARATOR + SEPARATOR);
                    }
                    properties.put(ADB_PATH_KEY, adbPath);
                    properties.put(TARGET_APK_KEY, apkPackage);
                    FileUtil.createPropertiesFile(propertiesPath, properties);
                    // refresh project
                    monitor.worked(1);
                    project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        };
        // run the new project creation operation
        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            System.out.println(e);
            return;
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof  ExecutionException && t.getCause() instanceof  CoreException) {
                CoreException cause = (CoreException) t.getCause();
                StatusAdapter status;
                if (cause.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
                    status = new StatusAdapter(StatusUtil.newStatus(IStatus.WARNING,NLS.bind(ResourceMessages.NewProject_caseVariantExistsError,project.getName()),cause));
                } else {
                    status = new StatusAdapter(StatusUtil.newStatus(cause.getStatus().getSeverity(),ResourceMessages.NewProject_errorMessage,cause));
                }
                status.setProperty(StatusAdapter.TITLE_PROPERTY, ResourceMessages.NewProject_errorMessage);
                StatusManager.getManager().handle(status, StatusManager.BLOCK);
            } else {
                StatusAdapter status = new StatusAdapter(new Status(IStatus.WARNING, IDEWorkbenchPlugin.IDE_WORKBENCH, 0, NLS.bind(ResourceMessages.NewProject_internalError, t.getMessage()), t));
                status.setProperty(StatusAdapter.TITLE_PROPERTY, ResourceMessages.NewProject_errorMessage);
                StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.BLOCK);
            }
            return;
        }
        newProject = project;
        return;
    }

    public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
        super.init(workbench, currentSelection);
        setNeedsProgressMonitor(true);
        setWindowTitle(ResourceMessages.NewProject_windowTitle);
    }

    protected void initializeDefaultPageImageDescriptor() {
        ImageDescriptor desc = IDEWorkbenchPlugin.getIDEImageDescriptor("wizban/newprj_wiz.png");
        setDefaultPageImageDescriptor(desc);
    }

    public boolean performFinish() {
        if (mainPage.adbPathField.getStringValue().equals("")) {
            // show alert
            return false;
        }
        createNewProject();
        if (newProject == null) {
            return false;
        }
        IWorkingSet[] workingSets = mainPage.getSelectedWorkingSets();
        getWorkbench().getWorkingSetManager().addToWorkingSets(newProject, workingSets);
        selectAndReveal(newProject);
        // save preference
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setValue(Scirocco2PreferenceInitializer.ANDROID_SDK_LOCATION, mainPage.adbPathField.getStringValue());
        return true;
    }

    public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
        configElement = cfig;
    }
}