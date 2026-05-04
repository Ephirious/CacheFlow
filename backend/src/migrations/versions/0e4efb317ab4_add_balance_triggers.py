"""add_balance_triggers

Revision ID: 0e4efb317ab4
Revises: d8bbd6440d79
Create Date: 2026-05-04 00:15:41.379445

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '0e4efb317ab4'
down_revision: Union[str, Sequence[str], None] = 'd8bbd6440d79'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    stmt = """
            CREATE OR REPLACE FUNCTION fn_sync_account_balance()
        RETURNS TRIGGER AS $$
        BEGIN
            IF (TG_OP = 'INSERT') THEN
                UPDATE accounts SET funds = funds + NEW.amount WHERE id = NEW.account_uuid;

            ELSIF (TG_OP = 'DELETE') THEN
                UPDATE accounts SET funds = funds - OLD.amount WHERE id = OLD.account_uuid;

            ELSIF (TG_OP = 'UPDATE') THEN
                IF (OLD.amount != NEW.amount OR OLD.account_uuid != NEW.account_uuid) THEN
                    UPDATE accounts SET funds = funds - OLD.amount WHERE id = OLD.account_uuid;
                    UPDATE accounts SET funds = funds + NEW.amount WHERE id = NEW.account_uuid;
                END IF;
            END IF;
            RETURN NULL;
        END;
        $$ LANGUAGE plpgsql;

        DROP TRIGGER IF EXISTS trg_apply_balance_changes ON operations;
        CREATE TRIGGER trg_apply_balance_changes
        AFTER INSERT OR UPDATE OR DELETE ON operations
        FOR EACH ROW EXECUTE FUNCTION fn_sync_account_balance();
            """
    op.execute(stmt)
    # ### end Alembic commands ###


def downgrade() -> None:
    """Downgrade schema."""
    stmt = """
    DROP TRIGGER IF EXISTS trg_apply_balance_changes ON operations;
    DROP FUNCTION IF EXISTS fn_sync_account_balance();
    """
    op.execute(stmt)
    # ### end Alembic commands ###
