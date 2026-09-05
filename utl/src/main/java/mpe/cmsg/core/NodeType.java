package mpe.cmsg.core;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NodeType implements INodeType {

    final INodeDesc iNodeDesc;

    public static NodeType of(INodeDesc iNodeDesc) {
        return new NodeType(iNodeDesc);
    }

    @Override
    public String stdTypeUC() {
        return this.iNodeDesc.stdTypeUC();
    }

    @Override
    public String stdTypeLC() {
        return this.iNodeDesc.stdTypeLC();
    }

    @Override
    public INodeDesc stdDesc() {
        return this.iNodeDesc;
    }
}
