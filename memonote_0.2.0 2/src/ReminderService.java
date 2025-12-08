// 如果你的其他类有 package 行，把它复制到这里最上面

import java.awt.Toolkit;
import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 后台提醒服务：
 * - 每分钟检查一次 memos.json
 * - 找到“到期日 = 今天”的 memo
 * - 在每天指定时间（REMIND_HOUR）弹出一次提醒
 */
public class ReminderService implements Runnable {

    // 每天几点提醒（24 小时制），比如 9 = 早上 9 点
    private static final int REMIND_HOUR = 9;

    private final MemoStore store = new MemoStore();
    private final Set<String> remindedIds = new HashSet<>(); // 今天已经提醒过的 id
    private LocalDate lastDate = null; // 记录上一次检查的日期

    @Override
    public void run() {
        System.out.println("[ReminderService] started.");

        while (true) {
            try {
                LocalDateTime now = LocalDateTime.now();
                LocalDate today = now.toLocalDate();

                // 日期变了（跨天）就清空已提醒列表
                if (lastDate == null || !today.equals(lastDate)) {
                    remindedIds.clear();
                    lastDate = today;
                    System.out.println("[ReminderService] new day: " + today);
                }

                int hour = now.getHour();
                int minute = now.getMinute();

                // 为了方便测试，这里给一点“时间窗口”：在 REMIND_HOUR 的前 5 分钟内都可以触发
                if (hour == REMIND_HOUR && minute < 5) {
                    checkAndRemind(today);
                }

                Thread.sleep(60_000); // 每 60 秒检查一次
            } catch (InterruptedException e) {
                System.out.println("[ReminderService] interrupted, stop.");
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** 检查今天到期、且还没提醒过的 Memo，并弹出提醒 */
    private void checkAndRemind(LocalDate today) {
        List<Memo> all = store.loadAll();
        if (all == null || all.isEmpty()) return;

        String todayStr = today.toString(); // 形如 "2025-11-24"

        for (Memo m : all) {
            String id = m.getId();
            String due = m.getDue();

            if (due == null || due.trim().isEmpty()) continue;   // 没有到期日
            if (!due.equals(todayStr)) continue;                  // 不是今天到期
            if (remindedIds.contains(id)) continue;               // 今天已经提醒过了

            showReminder(m);              // 弹窗 + 控制台
            remindedIds.add(id);          // 标记为今天已提醒
        }
    }

    /** 具体的提醒方式：控制台 + 蜂鸣 + 弹窗 */
    private void showReminder(Memo memo) {
        String text = "今天要做： " + memo.getTitle();

        // 控制台输出（可以在 IDEA 里看到）
        System.out.println("🔔 Memo Reminder: " + text);

        try {
            // 蜂鸣一下（有些系统可能听不到，但不会报错）
            Toolkit.getDefaultToolkit().beep();

            // 弹出一个对话框（网页不开也会弹，只要 Java 程序在跑）
            JOptionPane.showMessageDialog(
                    null,
                    text,
                    "Memo 提醒",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            // 如果因为环境原因弹窗失败，也不要让程序崩
            e.printStackTrace();
        }
    }
}
