import jdk.nashorn.internal.objects.NativeUint8Array;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class GUI extends JFrame{
    GridLayout outerGrid,buttonGrid,labelGrid;
    JPanel buttonContainer,labelContainer,outerContainer;
    JMenuBar menuBar;
    JMenu start,difficult,PSource;
    JMenuItem easy,normal,hard,custom,crazy,douM,go,pause,restart,saveSource,uploadSource,checkGlobalSource,checkLANSource;
    JFrame window = this;
    JTextArea information;
    JScrollPane scrollPane;
    JButton button[][];
    difficultListener difficultListener = new difficultListener();
    buttonListener buttonListener = new buttonListener();
    saveListener saveListener = new saveListener();
    controlListener controlListener = new controlListener();
    int level = 0;
    int allCount = 0,nowCount = 0,errorCount = 0;
    int source = 0;
    String buffer;

    public GUI(){
        initGameOperation();
    }

    public void resetGUI(){
        if(outerContainer != null){
            outerContainer.setVisible(false);
        }
        if (buttonContainer != null){
            buttonContainer.setVisible(false);
        }
        if(labelContainer != null){
            labelContainer.setVisible(false);
        }
    }

    void initGameBox(){
        allCount = 0;
        nowCount = 0;
        errorCount = 0;
        source = 0;
        PSource.setText("得分 " + source);
        saveSource.setEnabled(false);
        uploadSource.setEnabled(false);
        if(buttonContainer == null){
            buttonContainer = new JPanel();
        }else{
            buttonContainer.removeAll();
            buttonContainer = new JPanel();
        }
        if(outerContainer == null){
            outerContainer = new JPanel();
        }else{
            outerContainer.removeAll();
            outerContainer = new JPanel();
        }
        if(labelContainer == null){
            labelContainer = new JPanel();
        }else{
            labelContainer.removeAll();
            labelContainer = new JPanel();
        }
        resetGUI();
        information = new JTextArea("请记忆屏幕上显示的所有按钮的位置\n");
        information.setEditable(false);
        scrollPane = new JScrollPane(information);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        buttonGrid = new GridLayout(level,level);
        labelGrid = new GridLayout(1,1);
        outerGrid = new GridLayout(2,1);
        buttonListener = new buttonListener();
        outerContainer.setLayout(outerGrid);
        buttonContainer.setLayout(buttonGrid);
        labelContainer.setLayout(labelGrid);
        button = new JButton[level][level];
        for(int i = 0;i < level;i++){
            for(int j = 0;j < level;j++){
                button[i][j] = new JButton();
                boolean visiable = GUI.caculateVisiableMark();
                button[i][j].setVisible(visiable);
                button[i][j].setBackground(Color.red);
                if(visiable){
                    allCount++;
                }
                button[i][j].setActionCommand("" + GUI.changeBooleanToInt(visiable) + "-" + i + "#" + j);
                button[i][j].addActionListener(buttonListener);
                buttonContainer.add(button[i][j]);
            }
        }
        add(outerContainer);
        outerContainer.add(buttonContainer);
        outerContainer.add(labelContainer);
        labelContainer.add(scrollPane);
        outerContainer.setVisible(true);
        buttonContainer.setVisible(true);
        labelContainer.setVisible(true);
        disableAllButton();
    }

    void initGameOperation(){
        menuBar = new JMenuBar();
        difficult = new JMenu("难度");
        start = new JMenu("测试");
        PSource = new JMenu("得分 " + source);
        easy = new JMenuItem("简单");
        normal = new JMenuItem("正常");
        hard = new JMenuItem("宗师");
        crazy = new JMenuItem("国服第一");
        douM = new JMenuItem("抖M");
        custom = new JMenuItem("自定义");
        go = new JMenuItem("开始");
        pause = new JMenuItem("暂停");
        restart = new JMenuItem("重新开始");
        uploadSource = new JMenuItem("上传得分到服务器");
        saveSource = new JMenuItem("保存得分");
        checkLANSource = new JMenuItem("查看本地保存得分");
        checkGlobalSource = new JMenuItem("查看全球得分排名");
        difficult.add(easy);
        difficult.add(normal);
        difficult.add(hard);
        difficult.add(crazy);
        difficult.add(douM);
        difficult.add(custom);
        start.add(go);
        start.add(pause);
        start.add(restart);
        PSource.add(saveSource);
        PSource.add(uploadSource);
        PSource.add(checkLANSource);
        PSource.add(checkGlobalSource);
        menuBar.add(start);
        menuBar.add(difficult);
        menuBar.add(PSource);
        setJMenuBar(menuBar);
        easy.addActionListener(difficultListener);
        normal.addActionListener(difficultListener);
        hard.addActionListener(difficultListener);
        crazy.addActionListener(difficultListener);
        douM.addActionListener(difficultListener);
        custom.addActionListener(difficultListener);
        go.addActionListener(controlListener);
        pause.addActionListener(controlListener);
        restart.addActionListener(controlListener);
        saveSource.addActionListener(saveListener);
        checkLANSource.addActionListener(saveListener);
        saveSource.setEnabled(false);
        uploadSource.setEnabled(false);
    }

    void showAllButton(){
        for(int i = 0;i < level;i++){
            for(int j = 0;j < level;j++){
                button[i][j].setVisible(true);
            }
        }
        information.setText("");
        enableAllButton();
    }

    void disableAllButton(){
        for(int i = 0;i < level;i++){
            for(int j = 0;j < level;j++){
                button[i][j].setEnabled(false);
            }
        }
    }

    void enableAllButton(){
        for(int i = 0;i < level;i++){
            for(int j = 0;j < level;j++){
                button[i][j].setEnabled(true);
                button[i][j].setBackground(Color.WHITE);
            }
        }
    }

    void initDisplay(int x,int y,int w,int h,String title){
        setBounds(x,y,w,h);
        setVisible(true);
        setTitle(title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    void selectAll(ResultSet resultSet){
        buffer = "";
        try{
            while (resultSet.next()){
                buffer = buffer + "玩家" +  resultSet.getString("username") + "：" + resultSet.getString("source") + "分，难度等级：" + resultSet.getString("level") + "\n";
            }
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null,"发生SQL缓存读取错误","内部错误",JOptionPane.ERROR_MESSAGE);
        }
    }

    static boolean caculateVisiableMark(){
        return new Random().nextInt(2) == 1;
    }

    static int changeBooleanToInt(boolean boolValue){
        if(boolValue){
            return 1;
        }else{
            return 0;
        }
    }

    private class WaitTime{
        Timer timer;
        public WaitTime(int second){
            timer = new Timer();
            timer.schedule(new AutoTask(),second * 1000);
        }
    }

    private class AutoTask extends TimerTask{
        public void run(){
            showAllButton();
        }
    }


    private class difficultListener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            saveSource.setEnabled(false);
            uploadSource.setEnabled(false);
            if(event.getSource() == easy){
                level = 3;
                difficult.setText("简单");
                resetGUI();
            }
            if(event.getSource() == normal){
                level = 4;
                difficult.setText("正常");
                resetGUI();
            }
            if(event.getSource() == hard){
                level = 7;
                difficult.setText("宗师");
                resetGUI();
            }
            if(event.getSource() == crazy){
                level = 9;
                difficult.setText("国服第一");
                resetGUI();
            }
            if(event.getSource() == douM){
                level = 20;
                difficult.setText("抖M");
                resetGUI();
            }
            if(event.getSource() == custom){
                level = 0;
                try {
                    level = Integer.valueOf(JOptionPane.showInputDialog(null, "请输入难度等级(最小值为1，推荐1-20，难度等级为数字)", "自定义难度", JOptionPane.QUESTION_MESSAGE));
                }catch (NumberFormatException e){
                    JOptionPane.showMessageDialog(null,"请输入数字！","内部错误",JOptionPane.ERROR_MESSAGE);
                }
                if(level == 0){
                    difficult.setText("难度");
                }else{
                    difficult.setText("自定义");
                    resetGUI();
                }
            }
        }
    }

    private class buttonListener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            if(DataCheck.check(event.getActionCommand(),"^1-(\\d*).(\\d*)$")){
                String[] position = DataCheck.getStringByPattern(event.getActionCommand(),"\\d*.\\d*$").split("#");
                nowCount++;
                int plusSource = nowCount * level;
                information.setText(information.getText() + "正确！获得" + plusSource + "分\n");
                source = source + plusSource;
                PSource.setText("得分 " + source);
                button[Integer.valueOf(position[0])][Integer.valueOf(position[1])].setBackground(Color.green);
                button[Integer.valueOf(position[0])][Integer.valueOf(position[1])].setEnabled(false);
                if(nowCount == allCount){
                    disableAllButton();
                    JOptionPane.showMessageDialog(null,"恭喜你，完成测试","恭喜",JOptionPane.INFORMATION_MESSAGE);
                    information.setText(information.getText() + "本次测试结束，您的得分为：" + source + "\n");
                    saveSource.setEnabled(true);
                    uploadSource.setEnabled(true);
                }
            }else if(DataCheck.check(event.getActionCommand(),"^0-(\\d*).(\\d*)$")){
                if(allCount == 0){
                    disableAllButton();
                    JOptionPane.showMessageDialog(null,"本次没有任何按钮被激活，请点击重新开始","提示",JOptionPane.INFORMATION_MESSAGE);
                }else{
                    errorCount++;
                    int minusSource = (errorCount + nowCount) * level;
                    information.setText(information.getText() + "错误！扣除" + minusSource + "分\n");
                    source = source - minusSource;
                    PSource.setText("得分 " + source);
                }
            }
        }
    }

    private class controlListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            saveSource.setEnabled(false);
            uploadSource.setEnabled(false);
            if(event.getSource() == go || event.getSource() == restart){
                if(level != 0){
                    resetGUI();
                    initGameBox();
                    new WaitTime(level);
                    information.setText("计时开始！你共有" + level + "秒时间来记忆\n");
                }else{
                    JOptionPane.showMessageDialog(null,"你还有没有选择难度哦","提示",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    private class saveListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            if(event.getSource()==saveSource){
                DbConnection dbc = new DbConnection();
                dbc.setDriverName("com.mysql.jdbc.Driver");
                dbc.setDriverType("jdbc:mysql");
                dbc.setDb("memory_test");
                dbc.setCharset(true,"UTF-8");
                Connection collection = dbc.connectToDb();
                C create = new C(collection);
                String name = JOptionPane.showInputDialog(null,"请输入您的大名","保存得分",JOptionPane.INFORMATION_MESSAGE );
                if(name == null){
                    return ;
                }
                if(name.equals("")){
                    JOptionPane.showMessageDialog(null, "用户名不能为空，保存失败", "内部错误", JOptionPane.ERROR_MESSAGE);
                    return ;
                }
                if(level != 0){
                    int checkMark = create.table("source").field("username,source,level").value("'" + name + "'," + source + "," + level).create();
                    if(checkMark == 1) {
                        JOptionPane.showMessageDialog(null, "保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                        saveSource.setEnabled(false);
                    }
                    try{
                        collection.close();
                    }catch (Exception e){

                    }
                }else{
                    JOptionPane.showMessageDialog(null, "成绩无效，不能保存", "内部错误", JOptionPane.ERROR_MESSAGE);
                }
            }
            if(event.getSource()==checkLANSource){
                DbConnection dbc = new DbConnection();
                dbc.setDriverName("com.mysql.jdbc.Driver");
                dbc.setDriverType("jdbc:mysql");
                dbc.setDb("memory_test");
                dbc.setCharset(true,"UTF-8");
                Connection collection = dbc.connectToDb();
                R read = new R(collection);
                ResultSet resultSet = read.table("source").order("source","DESC").read();
                selectAll(resultSet);
                JOptionPane.showMessageDialog(null,buffer,"本地排名",JOptionPane.INFORMATION_MESSAGE);
                try{
                    collection.close();
                }catch (Exception e){

                }
            }
        }
    }
}