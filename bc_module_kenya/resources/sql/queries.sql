-- name: check-if-schema-exists
-- Does a simple count to check if schema is available
select count(*) from information_schema.tables where table_name='tbl_sms_tracker';

-- name: app-schema
-- Create sequence
CREATE SEQUENCE seq_sms_tracker_request_id START 1000001;

--Create parent table
CREATE TABLE tbl_sms_tracker (
    subscriber_fk bigint,
	event_time timestamp without time zone DEFAULT '-infinity'::timestamp without time zone NOT NULL,
	record_flag boolean DEFAULT false NOT NULL,
	batch_date timestamp without time zone DEFAULT now() NOT NULL,
	message_type smallint,
	amount integer,
	request_id bigint NOT NULL DEFAULT nextval('seq_sms_tracker_request_id'::regclass)
);

--Create new table to hold records to be worked on
CREATE TABLE tbl_sms_tracker_new (

) INHERITS(tbl_sms_tracker);

CREATE UNIQUE INDEX tbl_sms_tracker_new_request_id_idx
  ON tbl_sms_tracker_new
  USING btree
  (request_id);

CREATE INDEX tbl_sms_tracker_new_subscriber_fk_idx
  ON tbl_sms_tracker_new
  USING btree
  (subscriber_fk);


-- Create table to hold already processed data
CREATE TABLE tbl_sms_tracker_old (

) INHERITS(tbl_sms_tracker);

CREATE INDEX tbl_sms_tracker_old_subscriber_fk_idx
  ON tbl_sms_tracker_old
  USING btree
  (subscriber_fk);

--Create proc first
CREATE OR REPLACE FUNCTION use_latest_entry() RETURNS trigger AS
$BODY$
BEGIN
	-- Abort transaction if table is wrong
	IF (TG_TABLE_NAME != 'tbl_sms_tracker_new') THEN
		RETURN NULL;
	-- Do the processing
	ELSE
		delete from tbl_sms_tracker_new where subscriber_fk = NEW.subscriber_fk;
		RAISE NOTICE 'Discarding old records - %', NEW.subscriber_fk;
		RETURN NEW;
	END IF;

	-- Abort transaction (including actual insert) if the above conditions are not met
	RETURN null;
END;
$BODY$
	LANGUAGE plpgsql VOLATILE
		COST 100;
ALTER FUNCTION use_latest_entry()
		OWNER TO postgres;


--Create trigger
CREATE TRIGGER delete_duplicates
    BEFORE INSERT ON tbl_sms_tracker_new
    FOR EACH ROW
    EXECUTE PROCEDURE use_latest_entry();


--Create primary key on tbl_sms_tracker_old
ALTER TABLE tbl_sms_tracker_old
	ADD CONSTRAINT request_id_pk
    PRIMARY KEY (request_id);

--Edit proc_process_sms_subs to exclude deletes replaced by the trigger

CREATE OR REPLACE FUNCTION move_data() RETURNS trigger AS
$BODY$
BEGIN
	-- Abort transaction if table is wrong
	IF (TG_TABLE_NAME != 'tbl_sms_tracker_new') THEN
	RAISE NOTICE 'Wrong table - %';
		RETURN NULL;
	-- Do the processing
	ELSE
		insert into tbl_sms_tracker_old (subscriber_fk, event_time, record_flag, batch_date, message_type, request_id, amount) values (OLD.subscriber_fk, now(), 't' , OLD.batch_date, OLD.message_type, OLD.request_id, OLD.amount);
		RAISE NOTICE 'Moving processed records - %', OLD.subscriber_fk;
		RETURN OLD;
	END IF;

	-- Abort transaction (including actual insert) if the above conditions are not met
	RETURN null;
END;
$BODY$
	LANGUAGE plpgsql VOLATILE
		COST 100;
ALTER FUNCTION move_data()
		OWNER TO postgres;

-- Create delete trigger
CREATE TRIGGER move_data
    BEFORE DELETE ON tbl_sms_tracker_new
    FOR EACH ROW
    EXECUTE PROCEDURE move_data();

-- Check if table is now present
select count(*) from information_schema.tables where table_name = 'tbl_sms_tracker';


-- name: delete-sub!
-- Query to delete sub after being processed from tbl_sms_tracker_new
delete from tbl_sms_tracker_new where subscriber_fk = :subscriber_fk;

-- name: get-subs
-- Query to get subs to be processed, defined on the config
select subscriber_fk, message_type, amount from tbl_sms_tracker_new limit :limit;