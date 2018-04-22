package entry.setup;

import common.parser.StringEnumTransform;
import common.tetfu.common.ColorType;
import core.field.Field;
import core.mino.Piece;
import core.srs.Rotate;
import entry.DropType;
import entry.setup.operation.*;
import exceptions.FinderParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetupSettings {
    private static final String DEFAULT_LOG_FILE_PATH = "output/last_output.txt";
    private static final String DEFAULT_OUTPUT_BASE_FILE_PATH = "output/setup.html";

    private String logFilePath = DEFAULT_LOG_FILE_PATH;
    private boolean isUsingHold = true;
    private boolean isCombination = false;
    private int numOfPieces = Integer.MAX_VALUE;
    private ExcludeType exclude = ExcludeType.Holes;
    private List<FieldOperation> addOperations = Collections.emptyList();
    private int maxHeight = -1;
    private List<String> patterns = new ArrayList<>();
    private Field initField = null;
    private Field needFilledField = null;
    private Field notFilledField = null;
    private Field marginField = null;
    private ColorType marginColorType = null;
    private ColorType fillColorType = null;
    private ColorType noHolesColorType = null;
    private DropType dropType = DropType.Softdrop;
    private String outputBaseFilePath = DEFAULT_OUTPUT_BASE_FILE_PATH;

    // ********* Getter ************
    public boolean isUsingHold() {
        return isUsingHold;
    }

    String getLogFilePath() {
        return logFilePath;
    }

    String getOutputBaseFilePath() {
        return outputBaseFilePath;
    }

    ExcludeType getExcludeType() {
        return exclude;
    }

    int getNumOfPieces() {
        return numOfPieces;
    }

    boolean isCombination() {
        return isCombination;
    }

    List<FieldOperation> getAddOperations() {
        return addOperations;
    }

    int getMaxHeight() {
        return maxHeight;
    }

    List<String> getPatterns() {
        return patterns;
    }

    boolean isOutputToConsole() {
        return true;
    }

    Field getInitField() {
        return initField;
    }

    Field getNeedFilledField() {
        return needFilledField;
    }

    Field getNotFilledField() {
        return notFilledField;
    }

    Field getMarginField() {
        return marginField;
    }

    ColorType getMarginColorType() {
        return marginColorType;
    }

    ColorType getFillColorType() {
        return fillColorType;
    }

    ColorType getNoHolesColorType() {
        return noHolesColorType;
    }

    DropType getDropType() {
        return dropType;
    }

    // ********* Setter ************
    void setUsingHold(Boolean isUsingHold) {
        this.isUsingHold = isUsingHold;
    }

    void setNumOfPieces(int numOfPieces) {
        this.numOfPieces = 0 < numOfPieces ? numOfPieces : Integer.MAX_VALUE;
    }

    void setLogFilePath(String path) {
        this.logFilePath = path;
    }

    void setOutputBaseFilePath(String path) {
        this.outputBaseFilePath = path;
    }

    void setCombination(boolean isCombination) {
        this.isCombination = isCombination;
    }

    private void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    void setField(Field initField, Field needFilledField, Field notFilledField, Field marginField, int maxHeight) {
        setMaxHeight(maxHeight);
        setInitField(initField);
        setNeedFilledField(needFilledField);
        setNotFilledField(notFilledField);
        setMarginField(marginField);
    }

    private void setInitField(Field field) {
        this.initField = field;
    }

    private void setNeedFilledField(Field field) {
        this.needFilledField = field;
    }

    private void setNotFilledField(Field field) {
        this.notFilledField = field;
    }

    private void setMarginField(Field field) {
        this.marginField = field;
    }

    void setMarginColorType(String marginColor) throws FinderParseException {
        try {
            this.marginColorType = parseToColor(marginColor);
        } catch (IllegalArgumentException e) {
            throw new FinderParseException("Unsupported margin color: value=" + marginColor);
        }
    }

    // The Tetris Company standardization
    private ColorType parseToColor(String color) {
        switch (color.trim().toLowerCase()) {
            case "i":
            case "cyan":
            case "cy":
                return ColorType.I;
            case "j":
            case "blue":
            case "bl":
                return ColorType.J;
            case "l":
            case "orange":
            case "or":
                return ColorType.L;
            case "o":
            case "yellow":
            case "ye":
                return ColorType.O;
            case "s":
            case "green":
            case "gr":
                return ColorType.S;
            case "t":
            case "purple":
            case "pu":
                return ColorType.T;
            case "z":
            case "red":
            case "re":
                return ColorType.Z;
            case "x":
            case "g":
            case "gray":
                return null;
            case "none":
            case "null":
            case "empty":
                return null;
            default:
                throw new IllegalArgumentException();
        }
    }

    void setFillColorType(String fillColor) throws FinderParseException {
        try {
            this.fillColorType = parseToColor(fillColor);
        } catch (IllegalArgumentException e) {
            throw new FinderParseException("Unsupported fill color: value=" + fillColor);
        }
    }

    void setNoHolesColorType(String noHolesColor) throws FinderParseException {
        try {
            this.noHolesColorType = parseToColor(noHolesColor);
        } catch (IllegalArgumentException e) {
            throw new FinderParseException("Unsupported no-holes color: value=" + noHolesColor);
        }
    }

    void setDropType(String type) throws FinderParseException {
        switch (type.trim().toLowerCase()) {
            case "soft":
            case "softdrop":
                this.dropType = DropType.Softdrop;
                return;
            case "hard":
            case "harddrop":
                this.dropType = DropType.Harddrop;
                return;
            default:
                throw new FinderParseException("Unsupported droptype: type=" + type);
        }
    }

    void setExcludeType(String type) throws FinderParseException {
        switch (type.trim().toLowerCase()) {
            case "hole":
            case "holes":
            case "all-hole":
            case "all-holes":
                this.exclude = ExcludeType.Holes;
                return;
            case "strict-hole":
            case "strict-holes":
                this.exclude = ExcludeType.StrictHoles;
                return;
            default:
                throw new FinderParseException("Unsupported droptype: type=" + type);
        }
    }

    void setAddOperations(List<String> values) throws FinderParseException {
        ArrayList<FieldOperation> operations = new ArrayList<>();

        Pattern pattern = Pattern.compile("(.*?)\\((.*?)\\)");

        for (String value : values) {
            value = value.trim().replace(" ", "");

            Matcher matcher = pattern.matcher(value);
            if (!matcher.find()) {
                throw new FinderParseException("Unsupported operation / format: add=" + value);
            }
            String command = matcher.group(1);
            String args = matcher.group(2);

            try {
                FieldOperation operation = createFieldOperation(command, args);
                operations.add(operation);
            } catch (Exception e) {
                throw new FinderParseException("Unsupported operation / command: add=" + value, e);
            }
        }

        this.addOperations = operations;
    }

    private FieldOperation createFieldOperation(String command, String args) throws FinderParseException {
        switch (command.toLowerCase()) {
            case "row": {
                int y = Integer.valueOf(args);
                if (y < 0) {
                    throw new FinderParseException("Unsupported operation / 0 <= y: y=" + y);
                }
                return new FilledFieldOperation(y);
            }
            case "clear": {
                return new ClearFieldOperation();
            }
            case "block": {
                String[] split = args.split(",");
                int x = Integer.valueOf(split[0]);
                if (x < 0 || 10 <= x) {
                    throw new FinderParseException("Unsupported piece operation / 0 <= x < 10: args=" + args);
                }

                int y = Integer.valueOf(split[1]);
                if (y < 0) {
                    throw new FinderParseException("Unsupported piece operation / 0 <= y: args=" + args);
                }
                return new SetBlockFieldOperation(x, y);
            }
            default: {
                Piece piece = StringEnumTransform.toPiece(command.toUpperCase().charAt(0));
                if (!command.contains("-")) {
                    throw new FinderParseException("Unsupported piece operation / no rotation: args=" + command);
                }

                Rotate rotate = toRotate(command.split("-")[1].toLowerCase());

                String[] split = args.split(",");
                int x = Integer.valueOf(split[0]);
                if (x < 0 || 10 <= x) {
                    throw new FinderParseException("Unsupported piece operation / 0 <= x < 10: args=" + args);
                }

                int y = Integer.valueOf(split[1]);
                if (y < 0) {
                    throw new FinderParseException("Unsupported piece operation / 0 <= y: args=" + args);
                }

                return new PutMinoOperation(piece, rotate, x, y);
            }
        }
    }

    private Rotate toRotate(String name) {
        switch (name) {
            case "0":
            case "spawn":
                return Rotate.Spawn;
            case "l":
            case "left":
                return Rotate.Left;
            case "2":
            case "reverse":
                return Rotate.Reverse;
            case "r":
            case "right":
                return Rotate.Right;
        }
        throw new IllegalArgumentException("No reachable");
    }
}

