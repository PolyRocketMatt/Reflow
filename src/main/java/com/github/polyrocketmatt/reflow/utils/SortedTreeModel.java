package com.github.polyrocketmatt.reflow.utils;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortedTreeModel extends DefaultTreeModel {

    public SortedTreeModel(DefaultMutableTreeNode root) {
        super(root);
    }

    @Override
    public void insertNodeInto(MutableTreeNode newChild, MutableTreeNode parent, int index) {
        super.insertNodeInto(newChild, parent, index);
    }

    public void sortChildren(MutableTreeNode node) {
        if (node.getChildCount() > 0) {
            List<MutableTreeNode> children = new java.util.ArrayList<>(Collections.list(node.children()).stream().map(o -> (MutableTreeNode) o).toList());
            children.sort((Comparator<TreeNode>) (o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
            children.forEach(node::remove);

            //  First we find the children without children
            List<MutableTreeNode> leafs = children.stream().filter(child -> child.getChildCount() == 0).toList();
            List<MutableTreeNode> branches = children.stream().filter(child -> child.getChildCount() > 0).toList();

            //  We first handle the branches
            for (MutableTreeNode child : branches) {
                node.insert(child, node.getChildCount());
                sortChildren(child);
            }

            //  Finally we add the leafs
            for (MutableTreeNode child : leafs)
                node.insert(child, node.getChildCount());
        }
    }

}
