import grt
import mforms
from text_output import TextOutputTab


QUERY = """USE smart_logistics;
SELECT id, origin, destination, cargo, weight, status, eta, carrier, created_at
FROM shipments
WHERE id IN ('SHP-LIVE-2001', 'SHP-LIVE-2002')
ORDER BY id;"""


def log(message):
    with open("/tmp/workbench_refresh_grid.log", "a") as handle:
        handle.write(message + "\n")


ATTEMPTS = {"count": 0}


def render_query():
    ATTEMPTS["count"] += 1
    editors = list(getattr(grt.root.wb, "sqlEditors", []))

    if not editors:
        log("waiting for SQL editor")
        return True

    editor = editors[0]
    if not editor.isConnected:
        log("waiting for editor connection")
        return True

    editor.defaultSchema = "smart_logistics"
    qbuffer = editor.activeQueryEditor
    qbuffer.replaceContents(QUERY)
    resultsets = editor.executeScript(QUERY)

    if resultsets:
        view = TextOutputTab("")
        dock = mforms.fromgrt(qbuffer.resultDockingPoint)
        dock.dock_view(view, "", 0)
        view.set_title("Live Shipment Rows")
        dock.select_view(view)

        for result in resultsets:
            columns = [column.name for column in result.columns]
            widths = [max(len(name), 16) for name in columns]

            header = " | ".join(name.ljust(widths[index]) for index, name in enumerate(columns))
            separator = "-+-".join("-" * width for width in widths)
            lines = [header, separator]

            ok = result.goToFirstRow()
            while ok:
                row = []
                for index in range(len(columns)):
                    value = result.stringFieldValue(index)
                    row.append((value or "NULL").ljust(widths[index]))
                lines.append(" | ".join(row))
                ok = result.nextRow()

            view.textbox.append_text("\n".join(lines) + "\n")

    log("executed live shipment query and opened shipment text output")
    return False


mforms.Utilities.add_timeout(0.5, render_query)
