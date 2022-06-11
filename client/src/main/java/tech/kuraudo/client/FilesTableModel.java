package tech.kuraudo.client;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FilesTableModel extends AbstractTableModel implements TableModelListener {

    public final static int TYPE = 0;
    public final static int NAME = 1;
    public final static int SIZE = 2;
    public final static int MODIFIED = 3;


    private final String[] columnNames = {
            "Type",
            "Name",
            "Size",
            "Modified"
    };

    private final List< FileInfo > data = new ArrayList<>();

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FileInfo fileInfo = data.get(rowIndex);
        String columnName = getColumnName(columnIndex);
        return fileInfo.getValueByAttrName(columnName);
    }

    @Override
    public Class< ? > getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        // TBD
    }

    public void updateList(Path path) {
        try {
            List< FileInfo > directories = Files.list(path)
                    .map(FileInfo::new)
                    .filter(o -> o.getType() == FileInfo.FileType.DIR)
                    .sorted(Comparator.comparing(FileInfo::getFileName))
                    .toList();

            List< FileInfo > files = Files.list(path)
                    .map(FileInfo::new)
                    .filter(o -> o.getType() == FileInfo.FileType.FILE)
                    .sorted(Comparator.comparing(FileInfo::getFileName))
                    .toList();

            data.clear();
            data.addAll(directories);
            data.addAll(files);
        } catch (IOException e) {
            throw new RuntimeException("Unable to update list of files.");
        }
    }
}
