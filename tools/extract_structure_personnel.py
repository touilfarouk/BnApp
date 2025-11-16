from __future__ import annotations

import ast
from pathlib import Path

SOURCE = Path(r"c:\Users\ASUS\AndroidStudioProjects\OrderApp\structure-and-personnel.sql")
OUTPUT = Path(r"c:\Users\ASUS\AndroidStudioProjects\OrderApp\remote-structure-personnel-full.sql")


def parse_rows() -> list[tuple]:
    rows: list[tuple] = []
    if not SOURCE.exists():
        raise FileNotFoundError(SOURCE)

    for raw_line in SOURCE.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.upper().startswith("INSERT"):
            continue
        if line.endswith(","):
            line = line[:-1]
        if line.endswith(";"):
            line = line[:-1]

        try:
            tup = ast.literal_eval(line)
        except (SyntaxError, ValueError):
            continue

        rows.append(tup)
    return rows


def escape_sql(value: str) -> str:
    return value.replace("'", "''")


def build_sql(rows: list[tuple]) -> str:
    structures: dict[str, int] = {}
    next_structure_id = 1000
    personnel_values: list[str] = []

    for row in rows:
        if len(row) < 16:
            continue
        personnel_id = int(row[0])
        last_name = str(row[12]).strip()
        first_name = str(row[13]).strip()
        role = str(row[14]).strip()
        structure_name = str(row[15]).strip()

        if not first_name and not last_name:
            full_name = ""
        else:
            full_name = " ".join(part for part in (first_name, last_name) if part).strip()

        if structure_name:
            structures.setdefault(structure_name, next_structure_id)
            if structures[structure_name] == next_structure_id:
                next_structure_id += 1
            structure_id = structures[structure_name]
        else:
            structure_id = "NULL"

        personnel_values.append(
            "    ({id}, '{full_name}', NULL, NULL, '{role}', {structure_id}, NOW(), NOW())".format(
                id=personnel_id,
                full_name=escape_sql(full_name),
                role=escape_sql(role),
                structure_id=structure_id,
            )
        )

    structure_values = [
        "    ({id}, '{name}', NULL)".format(
            id=structure_id,
            name=escape_sql(name),
        )
        for name, structure_id in sorted(structures.items(), key=lambda item: item[1])
    ]

    sql_parts: list[str] = [
        "-- Auto-generated seed data for structures and personnel",
        "INSERT INTO structures (id, name, address)",
        "VALUES",
        ",\n".join(structure_values),
        "ON DUPLICATE KEY UPDATE",
        "    name = VALUES(name),",
        "    address = VALUES(address);",
        "",
        "INSERT INTO personnel (id, full_name, email, phone, role, structure_id, created_at, updated_at)",
        "VALUES",
        ",\n".join(personnel_values),
        "ON DUPLICATE KEY UPDATE",
        "    full_name = VALUES(full_name),",
        "    email = VALUES(email),",
        "    phone = VALUES(phone),",
        "    role = VALUES(role),",
        "    structure_id = VALUES(structure_id),",
        "    updated_at = VALUES(updated_at);",
    ]

    return "\n".join(sql_parts) + "\n"


def main() -> None:
    rows = parse_rows()
    sql = build_sql(rows)
    OUTPUT.write_text(sql, encoding="utf-8")


if __name__ == "__main__":
    main()
