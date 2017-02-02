package com.jasonpilbrough.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.jasonpilbrough.helper.SmartJButton;
import com.jasonpilbrough.helper.SmartJComboBox;
import com.jasonpilbrough.helper.SmartJTextField;
import com.jasonpilbrough.vcontroller.Controller;
import com.mysql.fabric.xmlrpc.base.Array;

import net.java.dev.designgridlayout.DesignGridLayout;

public class SearchView extends JFrame implements Drawable {

	private SmartJComboBox<String> combobox;
	private SmartJTextField filterTxt;
	private SmartJButton filterBtn, delete;
	private JTable table;
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch(evt.getPropertyName()){
			case "combo_model":
				combobox.setModel((ComboBoxModel)evt.getNewValue());
				draw();
				break;
			case "table_model":
				table.setModel((TableModel)evt.getNewValue());
				draw();
			break;
			default:
				throw new RuntimeException("Property " + evt.getPropertyName() + " not registered with view");
		}

	}

	@Override
	public void initialise(Controller controller) {
		setVisible(true);
	    setBounds(0, 0, 600, 425);
	    setTitle("Search");
	    
	    table = new JTable();
	    table.setGridColor(new java.awt.Color(218, 218, 218));
	    table.setShowVerticalLines(false);
		table.setAutoCreateRowSorter(true);
	    
	    combobox = new SmartJComboBox<>("box selection").withRegisteredController(controller);
       
	    delete = new SmartJButton("Delete").withRegisteredController(controller);
	    filterBtn = new SmartJButton("Filter Search").withRegisteredController(controller);
	    filterTxt = new SmartJTextField();
	    
	    //init command from view causes the model to push all values view
	    controller.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "init"));
	    
	}

	@Override
	public void draw() {
		JPanel parent = new JPanel();
        
		
		filterTxt = new SmartJTextField().withSomeState(filterTxt);
		filterBtn = new SmartJButton().withSomeState(filterBtn);
		delete = new SmartJButton().withSomeState(delete);
		combobox = new SmartJComboBox<String>().withSomeState(combobox);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(500,300));
		scrollPane.setViewportView(table);
		
		//remove the id column from the table
		//TODO dont like this, too tightly coupled to model with column count
		//if(table.getColumnName(0).equalsIgnoreCase("id"))
			//table.removeColumn(table.getColumnModel().getColumn(0));
        
		resizeColumns();
		setLeftAlignment();
		
        DesignGridLayout layout = new DesignGridLayout(parent);
        layout.row().grid().add(combobox).add(filterTxt,2).add(filterBtn);
        layout.row().grid().add(scrollPane);
        layout.row().grid().empty().empty().empty().add(delete);
        getContentPane().removeAll();
        getContentPane().add(parent);
        revalidate();

	}

	@Override
	public Map<String, Object> getFields() {
		Map<String,Object> map = new HashMap<>();
		map.put("selected_table", combobox.getSelectedItem().toString());
		map.put("filter", filterTxt.getText());
		int[] ids = new int[table.getSelectedRows().length];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = (int)(long) table.getValueAt(table.getSelectedRows()[i], 0);
		}
		map.put("selected_ids", ids);
		return map;
	}
	
	private void setLeftAlignment(){
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.LEFT);
		for (int i = 0; i < table.getColumnCount(); i++) {
			if(table.getColumnModel().getColumn(i).getIdentifier().toString().equalsIgnoreCase("id")){
				table.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
			}
			
		}
		
	}
	
	//SUMS 100%
	float[] columnWidthPercentage = {50.0f, 25.0f, 25.0f};
			
	private void resizeColumns() {
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	    int tW = table.getWidth();
	    TableColumn column;
	    TableColumnModel jTableColumnModel = table.getColumnModel();
	    int cantCols = jTableColumnModel.getColumnCount();
	    for (int i = 0; i < cantCols; i++) {
	        column = jTableColumnModel.getColumn(i);
	        int pWidth = Math.round((i==0?5:95/table.getColumnCount()) * tW);
	        //column.setMinWidth(pWidth);
	        //column.setMaxWidth(pWidth);
	        column.setPreferredWidth(pWidth);
	      
	    }
	}

}
