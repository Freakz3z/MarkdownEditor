package MarkdownEditor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MarkdownEditor extends JFrame {
    private JTextArea textArea;
    private JEditorPane htmlPane;
    private MarkdownConverter converter;
    private static final String TEMP_FILE_PATH = "temp.txt"; // 临时文件路径

    public MarkdownEditor() {
        setTitle("Markdown编辑器");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 初始化 MarkdownConverter
        converter = new MarkdownConverter();

        // 创建文本区域
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Dialog", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);

        // 创建 HTML 显示区域
        htmlPane = new JEditorPane();
        htmlPane.setContentType("text/html");
        htmlPane.setEditable(false);
        JScrollPane htmlScrollPane = new JScrollPane(htmlPane);

        // 创建分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, htmlScrollPane);
        splitPane.setDividerLocation(400);
        add(splitPane, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();

        // 导出为 HTML 按钮
        JButton exportHtmlButton = new JButton("导出为 HTML");
        exportHtmlButton.addActionListener(e -> {
            String htmlContent = converter.convert(textArea.getText());
            FileExporter.exportHtml(htmlContent, this);
        });
        buttonPanel.add(exportHtmlButton);

        // 导出为 MD 按钮
        JButton exportMdButton = new JButton("导出为 MD");
        exportMdButton.addActionListener(e -> FileExporter.exportFile(textArea.getText(), "md", "Markdown 文件", this));
        buttonPanel.add(exportMdButton);

        // 导出为 TXT 按钮
        JButton exportTxtButton = new JButton("导出为 TXT");
        exportTxtButton.addActionListener(e -> FileExporter.exportFile(textArea.getText(), "txt", "文本文件", this));
        buttonPanel.add(exportTxtButton);

        // 添加按钮面板到窗口底部
        add(buttonPanel, BorderLayout.SOUTH);

        // 监听文本区域的变化
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePreview();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePreview();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePreview();
            }
        });

        // 加载临时文件内容
        loadTempFile();

        // 添加窗口关闭监听器
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveTempFile();
                super.windowClosing(windowEvent);
            }
        });
    }

    // 更新 HTML 预览
    private void updatePreview() {
        String markdownText = textArea.getText();
        String html = converter.convert(markdownText);
        htmlPane.setText(html);
    }

    // 加载临时文件内容
    private void loadTempFile() {
        File tempFile = new File(TEMP_FILE_PATH);
        if (tempFile.exists()) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(TEMP_FILE_PATH)));
                textArea.setText(content);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "加载临时文件失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 保存当前文本到临时文件
    private void saveTempFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE_PATH))) {
            writer.write(textArea.getText());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "保存临时文件失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MarkdownEditor editor = new MarkdownEditor();
            editor.setVisible(true);
        });
    }
}