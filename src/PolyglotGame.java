import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PolyglotGame {
    private static final List<String> words = new ArrayList<>();

    static {
        words.add("кот");
        words.add("собака");
        words.add("дом");
        words.add("дерево");
        words.add("машина");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PolyglotGame::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Полиглот Игра");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new CardLayout());

        JPanel menuPanel = createMenuPanel(frame);
        JPanel gamePanel = createGamePanel(frame);
        JPanel addWordsPanel = createAddWordsPanel(frame);

        frame.add(menuPanel, "Menu");
        frame.add(gamePanel, "Game");
        frame.add(addWordsPanel, "AddWords");

        frame.setVisible(true);
    }

    private static JPanel createMenuPanel(JFrame frame) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton startGameButton = new JButton("Начать игру");
        JButton showInstructionsButton = new JButton("Показать инструкции");
        JButton addWordsButton = new JButton("Добавить слова");
        JButton exitButton = new JButton("Выйти");

        startGameButton.addActionListener(e -> switchToPanel(frame, "Game"));
        showInstructionsButton.addActionListener(e -> showInstructions());
        addWordsButton.addActionListener(e -> switchToPanel(frame, "AddWords"));
        exitButton.addActionListener(e -> System.exit(0));

        panel.add(startGameButton);
        panel.add(showInstructionsButton);
        panel.add(addWordsButton);
        panel.add(exitButton);

        return panel;
    }

    private static JPanel createGamePanel(JFrame frame) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel instructionLabel = new JLabel("Угадайте слово по перемешанным буквам:");
        JLabel scrambledWordLabel = new JLabel("");
        JTextField answerField = new JTextField();
        JButton submitButton = new JButton("Отправить");
        JButton backButton = new JButton("Назад в меню");

        GameSession gameSession = new GameSession();

        answerField.setMaximumSize(new Dimension(Integer.MAX_VALUE, answerField.getPreferredSize().height));

        submitButton.addActionListener(ev -> {
            String answer = answerField.getText().trim();
            if (answer.equalsIgnoreCase(gameSession.getCurrentWord())) {
                JOptionPane.showMessageDialog(frame, "Верно!");
            } else {
                JOptionPane.showMessageDialog(frame, "Неверно. Правильный ответ: " + gameSession.getCurrentWord());
            }

            if (gameSession.hasNextWord()) {
                gameSession.nextWord();
                scrambledWordLabel.setText(scrambleWord(gameSession.getCurrentWord()));
            } else {
                JOptionPane.showMessageDialog(frame, "Игра окончена!");
                switchToPanel(frame, "Menu");
            }
            answerField.setText("");
        });

        backButton.addActionListener(e -> switchToPanel(frame, "Menu"));

        panel.add(instructionLabel);
        panel.add(scrambledWordLabel);
        panel.add(answerField);
        panel.add(submitButton);
        panel.add(backButton);

        // Инициализация игры
        if (gameSession.hasNextWord()) {
            gameSession.nextWord();
            scrambledWordLabel.setText(scrambleWord(gameSession.getCurrentWord()));
        }

        return panel;
    }

    private static JPanel createAddWordsPanel(JFrame frame) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel instructionLabel = new JLabel("Введите новые слова:");
        JTextField wordField = new JTextField();
        JButton addButton = new JButton("Добавить");
        JButton backButton = new JButton("Назад в меню");

        wordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, wordField.getPreferredSize().height));

        addButton.addActionListener(e -> {
            String newWord = wordField.getText().trim();
            if (!newWord.isEmpty()) {
                words.add(newWord);
                JOptionPane.showMessageDialog(frame, "Слово '" + newWord + "' добавлено.");
                wordField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Пустое слово не может быть добавлено.");
            }
        });

        backButton.addActionListener(e -> switchToPanel(frame, "Menu"));

        panel.add(instructionLabel);
        panel.add(wordField);
        panel.add(addButton);
        panel.add(backButton);

        return panel;
    }

    private static void switchToPanel(JFrame frame, String panelName) {
        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), panelName);
    }

    private static void showInstructions() {
        JOptionPane.showMessageDialog(null,
                "1. В этой игре вам нужно угадать слова по перемешанным буквам.\n" +
                        "2. Для выхода из игры введите 'exit'.\n" +
                        "3. Ваш счет будет показан в конце игры.");
    }

    private static String scrambleWord(String word) {
        List<Character> characters = new ArrayList<>();
        for (char c : word.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);
        StringBuilder scrambledWord = new StringBuilder();
        for (char c : characters) {
            scrambledWord.append(c);
        }
        return scrambledWord.toString();
    }

    private static class GameSession {
        private final Iterator<String> wordIterator;
        private String currentWord;

        public GameSession() {
            List<String> shuffledWords = new ArrayList<>(words);
            Collections.shuffle(shuffledWords);
            this.wordIterator = shuffledWords.iterator();
        }

        public boolean hasNextWord() {
            return wordIterator.hasNext();
        }

        public void nextWord() {
            if (hasNextWord()) {
                currentWord = wordIterator.next();
            }
        }

        public String getCurrentWord() {
            return currentWord;
        }
    }
}
