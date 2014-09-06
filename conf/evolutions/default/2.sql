# --- Sample dataset

# --- !Ups

-- Nicolas

insert into user (id, email, name, password, is_transporter, is_shipper, description, address)
  values (  1, 'nicolas@lunatech.com', 'Lunatech', '1q2w3e', false, true, 'A company that creates software', 'Heemraadssingel 70, 3021DD Rotterdam');
insert into user (id, email, name, password, is_transporter, is_shipper, description, address)
  values (  2, 'mvo.home@chello.nl', 'The big truck company', '1q2w3e', false, true, 'A company that transport goods', 'Heemraadssingel 30, 3021DD Rotterdam');


insert into shipment (id, reference, from_address, to_address, volume, weight, pieces, booked, expiration_date, pickup_time, owner_id, transporter_id)
  values (  1, '12031221', 'Heemraadssingel 70, 3021DD Rotterdam', '70 Bd Gambetta, 06000 Nice', 1.03, 2.1, 2, false, '2014-09-30', null, 1, null);

insert into shipment (id, reference, from_address, to_address, volume, weight, pieces, booked, expiration_date, pickup_time, owner_id, transporter_id)
  values (  2, '12031221', 'Heemraadssingel 70, 3021DD Rotterdam', '70 Bd Gambetta, 06000 Nice', 1.03, 2.1, 2, false, '2014-09-30', null, 1, null);

# --- !Downs
delete from shipment;
delete from user;
