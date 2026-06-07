package bm.b0b0b0.SoulPact.bank.config.settings;

import java.util.ArrayList;
import java.util.List;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;
import net.elytrium.serializer.language.object.YamlSerializable;

@Comment(@CommentValue("SoulPact-Bank — казна клана (лимиты, GUI). Тексты — в lang/"))
public final class BankSettings extends YamlSerializable {

    public BankSettings() {
        super(BankSerializerConfig.INSTANCE);
    }

    @Comment(@CommentValue("Язык по умолчанию (имя файла без .yml из папки lang/)"))
    public String locale = "ru";

    @Comment(@CommentValue("Запасной язык, если ключ не найден в основном"))
    public String fallbackLocale = "en";

    @NewLine
    @Comment(@CommentValue("Минимальная сумма операции"))
    public double minAmount = 1D;

    @Comment(@CommentValue("Максимальный депозит за раз"))
    public double maxDeposit = 1_000_000D;

    @Comment(@CommentValue("Максимальное снятие за раз"))
    public double maxWithdraw = 1_000_000D;

    @Comment(@CommentValue("Уведомление клану при депозите выше порога"))
    public double notifyDepositAbove = 1000D;

    @Comment(@CommentValue("Размер топа вкладчиков"))
    public int contributorTopSize = 5;

    @Comment(@CommentValue("Строк журнала в GUI"))
    public int ledgerPreviewSize = 8;

    @NewLine
    @Comment(@CommentValue("Пресеты депозита"))
    public List<Double> depositPresets = new ArrayList<>(List.of(100D, 1000D, 10000D));

    @Comment(@CommentValue("Пресеты снятия"))
    public List<Double> withdrawPresets = new ArrayList<>(List.of(100D, 1000D, 10000D));

    @NewLine
    @Comment(@CommentValue("Инвентарь казны"))
    public BankGuiSettings gui = new BankGuiSettings();
}
