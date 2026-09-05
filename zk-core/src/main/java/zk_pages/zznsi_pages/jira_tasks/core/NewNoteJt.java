package zk_pages.zznsi_pages.jira_tasks.core;

import mpe.cmsg.core.NewNode;
import mpu.pare.Pare;
import org.zkoss.zul.Window;
import zk_notes.factory.NFForm;
import zk_notes.node.NodeDir;
import zk_notes.node_state.AppStateFactory;
import zk_notes.node_state.impl.FormState;
import zk_os.core.Sdn;

import java.util.Optional;

public class NewNoteJt extends NewNode {

    public final String[] hlp;

    public NewNoteJt(String[] hlp, Pare<String, String> sdn, String formName) {
        super(sdn, formName);
        this.hlp = hlp;
    }

    public NodeDir nodeDir() {
        return NodeDir.ofNodeName(sdn, nodeName());
    }

    public boolean isNewIssueInTree() {
        return RecoveryState.getRecModel(nodeName, null) == null;
    }

    public Window writeFormAndOpenWindow(Optional<String> data, Optional<String> state) {
        return writeForm(data, state, null).openRequired();
    }

    private FormState props;

    @Override
    public NewNoteJt writeForm(Optional<String> data, Optional<String> dataProps, Optional<String> linkProps) {
        Pare<String, String> sdn0 = sdn == null ? Sdn.get() : sdn;
        props = AppStateFactory.ofFormName_WithContent(sdn0, nodeName, data==null?null:data.get(), dataProps==null?null:dataProps.get(), true);
        return this;
    }

    //
    //

    public Window openRequired() {
        return NFForm.openFormRequired(sdn, nodeName);
    }

    public static Window openNewWithState(Pare<String, String> sdn, String nodeName, String data, String state) {
        return NFForm.openFormRequired(sdn, nodeName);
    }

    @Override
    public boolean isNewProps() {
        return !formState().existPropsFile();
    }

    @Override
    public boolean isNewData() {
        return !formState().existPropsFile(true);
    }

    public FormState formState() {
        return FormState.ofName(sdn, nodeName());
    }

}
