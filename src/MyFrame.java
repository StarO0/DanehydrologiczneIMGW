import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class MyFrame extends JFrame implements ActionListener {
    private JComboBox<String> comboVoivodeship;
    private JComboBox<String> comboRiver;
    private JButton submitButton;
    private JTable table;
    private DefaultTableModel model;
    private JSONArray jsonArray;

    private final String[] headers = {
            "id_stacji" , "stacja" , "rzeka" , "województwo" ,
            "stan_wody" , "stan_alarmowy" , "stan_ostrzegawczy" ,
            "temperatura_wody" , "zjawisko_lodowe" , "zjawisko_zarastania" ,
            "data_pomiaru" , "kod_stacji"
    };

    public MyFrame() throws Exception {
        super("Dane hydrologiczne IMGW");

        setLayout(new BorderLayout(10 , 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Wybierz województwo:"));

        comboVoivodeship = new JComboBox<>();
        comboVoivodeship.addActionListener(this);
        topPanel.add(comboVoivodeship);

        topPanel.add(new JLabel("Wybierz rzekę:"));
        comboRiver = new JComboBox<>();
        topPanel.add(comboRiver);

        submitButton = new JButton("Pokaż dane");
        submitButton.addActionListener(this);
        topPanel.add(submitButton);

        add(topPanel , BorderLayout.NORTH);

        model = new DefaultTableModel(headers , 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane , BorderLayout.CENTER);

        ImgwApi api = new ImgwApi();
        jsonArray = api.fetchHydroData();

        Set<String> voivodeships = new TreeSet<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject station = jsonArray.getJSONObject(i);
            if (ImgwApi.hasValidVoivodeship(station)) {
                voivodeships.add(station.getString("wojewodztwo"));
            }
        }
        for (String woj : voivodeships) {
            comboVoivodeship.addItem(woj);
        }

        setSize(1200 , 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == comboVoivodeship) {
            comboRiver.removeAllItems();
            String selectedWoj = Objects.requireNonNull(comboVoivodeship.getSelectedItem()).toString().toLowerCase();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject station = jsonArray.getJSONObject(i);
                String woj = station.optString("wojewodztwo" , "").toLowerCase();
                if (woj.contains(selectedWoj)) {
                    comboRiver.addItem(station.optString("rzeka" , "-"));
                }
            }
        }

        if (e.getSource() == submitButton) {
            model.setRowCount(0);
            String selectedWoj = Objects.requireNonNull(comboVoivodeship.getSelectedItem()).toString().toLowerCase();
            String selectedRiver = Objects.requireNonNull(comboRiver.getSelectedItem()).toString().toLowerCase();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject station = jsonArray.getJSONObject(i);
                String woj = station.optString("wojewodztwo" , "").toLowerCase();
                String rzeka = station.optString("rzeka" , "").toLowerCase();

                if (woj.contains(selectedWoj) && rzeka.contains(selectedRiver)) {
                    model.addRow(new Object[]{
                            station.opt("id_stacji") ,
                            station.opt("stacja") ,
                            station.opt("rzeka") ,
                            station.opt("wojewodztwo") ,
                            station.opt("stan_wody") ,
                            station.opt("stan_alarmowy") ,
                            station.opt("stan_ostrzegawczy") ,
                            station.opt("temperatura_wody") ,
                            station.opt("zjawisko_lodowe") ,
                            station.opt("zjawisko_zarastania") ,
                            station.opt("data_pomiaru") ,
                            station.opt("kod_stacji")
                    });
                }
            }
        }
    }
}
