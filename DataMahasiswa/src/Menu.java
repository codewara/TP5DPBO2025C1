import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Menu extends JFrame{
    public static void main(String[] args) {
        Menu window = new Menu();
        window.setSize(720, 560);
        window.setLocationRelativeTo(null);
        window.setContentPane(window.mainPanel);
        window.getContentPane().setBackground(Color.white);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private int selectedIndex = -1;
    private ArrayList<Mahasiswa> listMahasiswa;
    private Database database;

    private JPanel mainPanel;
    private JTable mahasiswaTable;
    private JLabel titleLabel;
    private JTextField nimField;
    private JTextField namaField;
    private JComboBox jenisKelaminComboBox;
    private JLabel nimLabel;
    private JLabel namaLabel;
    private JLabel jenisKelaminLabel;
    private JLabel tanggalLahirLabel;
    private JComboBox tanggalComboBox;
    private JComboBox bulanComboBox;
    private JComboBox tahunComboBox;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JButton deleteButton;

    private void resize() {
        mahasiswaTable.getColumnModel().getColumn(0).setPreferredWidth(25);
        mahasiswaTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        mahasiswaTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        mahasiswaTable.getColumnModel().getColumn(3).setPreferredWidth(85);
        mahasiswaTable.getColumnModel().getColumn(4).setPreferredWidth(85);
    }

    private void refresh() {
        try {
            listMahasiswa.clear();
            mahasiswaTable.setModel(setTable());
            ResultSet resultSet = database.selectQuery("SELECT * FROM mahasiswa");
            while (resultSet.next()) {
                Mahasiswa mahasiswa = new Mahasiswa(
                    resultSet.getString("nim"),
                    resultSet.getString("nama"),
                    resultSet.getString("jenis_kelamin"),
                    resultSet.getString("tanggal_lahir")
                );
                listMahasiswa.add(mahasiswa);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Menu() {
        database = new Database();
        listMahasiswa = new ArrayList<>();
        refresh();
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        resize();

        String[] jenisKelaminData = {"", "Laki-laki", "Perempuan"};
        jenisKelaminComboBox.setModel(new DefaultComboBoxModel<>(jenisKelaminData));

        String[] tanggalData = {"", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
                                "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
        tanggalComboBox.setModel(new DefaultComboBoxModel<>(tanggalData));

        String[] bulanData = {"", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                              "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        bulanComboBox.setModel(new DefaultComboBoxModel<>(bulanData));

        String[] tahunData = {"", "2000", "2001", "2002", "2003", "2004", "2005", "2006",
                              "2007", "2008", "2009", "2010", "2011", "2012", "2013",
                              "2014", "2015", "2016", "2017", "2018", "2019", "2020"};
        tahunComboBox.setModel(new DefaultComboBoxModel<>(tahunData));

        deleteButton.setVisible(false);

        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) insertData();
                else updateData();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex >= 0) deleteData();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        mahasiswaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed (MouseEvent e) {
            selectedIndex = mahasiswaTable.getSelectedRow();
            String selectedNim = mahasiswaTable.getModel().getValueAt(selectedIndex, 1).toString();
            String selectedNama = mahasiswaTable.getModel().getValueAt(selectedIndex, 2).toString();
            String selectedJenisKelamin = mahasiswaTable.getModel().getValueAt(selectedIndex, 3).toString();
            String selectedTanggalLahir = mahasiswaTable.getModel().getValueAt(selectedIndex, 4).toString();
            nimField.setText(selectedNim);
            namaField.setText(selectedNama);
            jenisKelaminComboBox.setSelectedItem(selectedJenisKelamin);
            String[] selectedTanggalLahirArray = selectedTanggalLahir.split("-");
            tanggalComboBox.setSelectedItem(selectedTanggalLahirArray[0]);
            bulanComboBox.setSelectedIndex(Integer.parseInt(selectedTanggalLahirArray[1]));
            tahunComboBox.setSelectedItem(selectedTanggalLahirArray[2]);
            addUpdateButton.setText("Update");
            deleteButton.setVisible(true);
            }
        });
    }

    public final DefaultTableModel setTable() {
        Object[] column = {"No", "NIM", "Nama", "Jenis Kelamin", "Tanggal Lahir"};
        DefaultTableModel temp = new DefaultTableModel(null, column);

        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM mahasiswa");

            int i = 0;
            while (resultSet.next()) {
                Object[] row = new Object[5];

                row[0] = i + 1;
                row[1] = resultSet.getString("nim");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getString("jenis_kelamin");
                row[4] = resultSet.getString("tanggal_lahir");

                temp.addRow(row);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return temp;
    }

    public void insertData() {
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String tanggalLahir = tanggalComboBox.getSelectedItem().toString() + "-" +
                              (bulanComboBox.getSelectedIndex() < 10 ? "0" + bulanComboBox.getSelectedIndex() : String.valueOf(bulanComboBox.getSelectedIndex())) + "-" +
                              tahunComboBox.getSelectedItem().toString();

        if (nim.isEmpty() || nama.isEmpty() || jenisKelaminComboBox.getSelectedIndex() == 0 || tanggalComboBox.getSelectedIndex() == 0 ||
            bulanComboBox.getSelectedIndex() == 0 || tahunComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Semua field harus diisi!");
            return;
        }

        for (Mahasiswa mahasiswa : listMahasiswa) {
            if (mahasiswa.getNim().equals(nim)) {
                JOptionPane.showMessageDialog(null, "NIM sudah ada!");
                return;
            }
        }

        String sql = "INSERT INTO mahasiswa " +
                     "VALUES (null, '" + nim + "', '" + nama + "', '" + jenisKelamin + "', '" + tanggalLahir + "');";
        database.insertUpdateaDeleteQuery(sql);
        refresh(); clearForm(); resize();

        System.out.println("Insert berhasil!");
        JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan!");
    }

    public void updateData() {
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String tanggalLahir = tanggalComboBox.getSelectedItem().toString() + "-" +
                              (bulanComboBox.getSelectedIndex() < 10 ? "0" + bulanComboBox.getSelectedIndex() : String.valueOf(bulanComboBox.getSelectedIndex())) + "-" +
                              tahunComboBox.getSelectedItem().toString();

        Mahasiswa currentMahasiswa = listMahasiswa.get(selectedIndex);
        if (currentMahasiswa.getNim().equals(nim) && currentMahasiswa.getNama().equals(nama) &&
            currentMahasiswa.getJenisKelamin().equals(jenisKelamin) && currentMahasiswa.getTanggalLahir().equals(tanggalLahir)) {
            JOptionPane.showMessageDialog(null, "Tidak ada perubahan data!");
            refresh(); clearForm(); resize();
            return;
        }

        if (nim.isEmpty() || nama.isEmpty() || jenisKelaminComboBox.getSelectedIndex() == 0 ||
            tanggalComboBox.getSelectedIndex() == 0 || bulanComboBox.getSelectedIndex() == 0 || tahunComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Semua field harus diisi!");
            return;
        }

        for (Mahasiswa mahasiswa : listMahasiswa) {
            if (mahasiswa == listMahasiswa.get(selectedIndex)) continue;
            else if (mahasiswa.getNim().equals(nim)) {
                JOptionPane.showMessageDialog(null, "NIM sudah ada!");
                return;
            }
        }

        String sql = "UPDATE mahasiswa SET " +
                     "nim = '" + nim + "'," +
                     "nama = '" + nama + "', " +
                     "jenis_kelamin = '" + jenisKelamin + "', " +
                     "tanggal_lahir = '" + tanggalLahir + "' " +
                     "WHERE nim = '" + listMahasiswa.get(selectedIndex).getNim() + "';";
        database.insertUpdateaDeleteQuery(sql);
        refresh(); clearForm(); resize();

        System.out.println("Update berhasil!");
        JOptionPane.showMessageDialog(null, "Data berhasil diubah!");
    }

    public void deleteData() {
        int dialogResult = JOptionPane.showConfirmDialog(null, "Hapus data?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM mahasiswa " +
                         "WHERE nim = '" + listMahasiswa.get(selectedIndex).getNim() + "';";
            database.insertUpdateaDeleteQuery(sql);
            refresh(); clearForm(); resize();

            System.out.println("Delete berhasil!");
            JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
        }
    }

    public void clearForm() {
        nimField.setText("");
        namaField.setText("");
        jenisKelaminComboBox.setSelectedIndex(0);
        tanggalComboBox.setSelectedIndex(0);
        bulanComboBox.setSelectedIndex(0);
        tahunComboBox.setSelectedIndex(0);
        addUpdateButton.setText("Add");
        deleteButton.setVisible(false);
        selectedIndex = -1;
    }
}