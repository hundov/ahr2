package util;

public class Logger {

    public boolean enabled = true;

    private static final StackWalker STACK_WALKER =
            StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public void send(String message) {
        if (!enabled) {
            return;
        }

        String caller = STACK_WALKER.walk(frames -> frames
                .skip(1)
                .findFirst()
                .map(frame ->
                        frame.getDeclaringClass().getSimpleName()
                                + "." +
                                frame.getMethodName()
                                + "()"
                )
                .orElse("Unknown"));

        System.out.println("[" + caller + "] " + message);
    }
}