package org.ruarchie.dev;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

public class DateEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    public JFormattedTextField jFTFdate = new JFormattedTextField();

    @Override
    public Object getCellEditorValue() {

        return jFTFdate.getText();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        jFTFdate.setText((String) value);
        jFTFdate.setColumns(10);

        MaskFormatter dateMask;
        try {
            dateMask = new MaskFormatter("##-##-####");

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        dateMask.install(jFTFdate);

        jFTFdate.addActionListener(this);
        return jFTFdate;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}

