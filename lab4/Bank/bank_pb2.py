# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: bank.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf.internal import enum_type_wrapper
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='bank.proto',
  package='bank',
  syntax='proto3',
  serialized_options=None,
  serialized_pb=_b('\n\nbank.proto\x12\x04\x62\x61nk\"3\n\x0f\x43urrencyRequest\x12 \n\x08\x63urrency\x18\x01 \x03(\x0e\x32\x0e.bank.Currency\";\n\rCurrencyReply\x12*\n\rcurrencyTable\x18\x01 \x03(\x0b\x32\x13.bank.CurrencyTable\"@\n\rCurrencyTable\x12 \n\x08\x63urrency\x18\x01 \x01(\x0e\x32\x0e.bank.Currency\x12\r\n\x05value\x18\x02 \x01(\x02*&\n\x08\x43urrency\x12\x07\n\x03PLN\x10\x00\x12\x07\n\x03USD\x10\x01\x12\x08\n\x04\x45URO\x10\x02\x32P\n\x0f\x43urrencyService\x12=\n\x0bgetCurrency\x12\x15.bank.CurrencyRequest\x1a\x13.bank.CurrencyReply\"\x00\x30\x01\x62\x06proto3')
)

_CURRENCY = _descriptor.EnumDescriptor(
  name='Currency',
  full_name='bank.Currency',
  filename=None,
  file=DESCRIPTOR,
  values=[
    _descriptor.EnumValueDescriptor(
      name='PLN', index=0, number=0,
      serialized_options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='USD', index=1, number=1,
      serialized_options=None,
      type=None),
    _descriptor.EnumValueDescriptor(
      name='EURO', index=2, number=2,
      serialized_options=None,
      type=None),
  ],
  containing_type=None,
  serialized_options=None,
  serialized_start=200,
  serialized_end=238,
)
_sym_db.RegisterEnumDescriptor(_CURRENCY)

Currency = enum_type_wrapper.EnumTypeWrapper(_CURRENCY)
PLN = 0
USD = 1
EURO = 2



_CURRENCYREQUEST = _descriptor.Descriptor(
  name='CurrencyRequest',
  full_name='bank.CurrencyRequest',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='currency', full_name='bank.CurrencyRequest.currency', index=0,
      number=1, type=14, cpp_type=8, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=20,
  serialized_end=71,
)


_CURRENCYREPLY = _descriptor.Descriptor(
  name='CurrencyReply',
  full_name='bank.CurrencyReply',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='currencyTable', full_name='bank.CurrencyReply.currencyTable', index=0,
      number=1, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=73,
  serialized_end=132,
)


_CURRENCYTABLE = _descriptor.Descriptor(
  name='CurrencyTable',
  full_name='bank.CurrencyTable',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='currency', full_name='bank.CurrencyTable.currency', index=0,
      number=1, type=14, cpp_type=8, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='value', full_name='bank.CurrencyTable.value', index=1,
      number=2, type=2, cpp_type=6, label=1,
      has_default_value=False, default_value=float(0),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=134,
  serialized_end=198,
)

_CURRENCYREQUEST.fields_by_name['currency'].enum_type = _CURRENCY
_CURRENCYREPLY.fields_by_name['currencyTable'].message_type = _CURRENCYTABLE
_CURRENCYTABLE.fields_by_name['currency'].enum_type = _CURRENCY
DESCRIPTOR.message_types_by_name['CurrencyRequest'] = _CURRENCYREQUEST
DESCRIPTOR.message_types_by_name['CurrencyReply'] = _CURRENCYREPLY
DESCRIPTOR.message_types_by_name['CurrencyTable'] = _CURRENCYTABLE
DESCRIPTOR.enum_types_by_name['Currency'] = _CURRENCY
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

CurrencyRequest = _reflection.GeneratedProtocolMessageType('CurrencyRequest', (_message.Message,), dict(
  DESCRIPTOR = _CURRENCYREQUEST,
  __module__ = 'bank_pb2'
  # @@protoc_insertion_point(class_scope:bank.CurrencyRequest)
  ))
_sym_db.RegisterMessage(CurrencyRequest)

CurrencyReply = _reflection.GeneratedProtocolMessageType('CurrencyReply', (_message.Message,), dict(
  DESCRIPTOR = _CURRENCYREPLY,
  __module__ = 'bank_pb2'
  # @@protoc_insertion_point(class_scope:bank.CurrencyReply)
  ))
_sym_db.RegisterMessage(CurrencyReply)

CurrencyTable = _reflection.GeneratedProtocolMessageType('CurrencyTable', (_message.Message,), dict(
  DESCRIPTOR = _CURRENCYTABLE,
  __module__ = 'bank_pb2'
  # @@protoc_insertion_point(class_scope:bank.CurrencyTable)
  ))
_sym_db.RegisterMessage(CurrencyTable)



_CURRENCYSERVICE = _descriptor.ServiceDescriptor(
  name='CurrencyService',
  full_name='bank.CurrencyService',
  file=DESCRIPTOR,
  index=0,
  serialized_options=None,
  serialized_start=240,
  serialized_end=320,
  methods=[
  _descriptor.MethodDescriptor(
    name='getCurrency',
    full_name='bank.CurrencyService.getCurrency',
    index=0,
    containing_service=None,
    input_type=_CURRENCYREQUEST,
    output_type=_CURRENCYREPLY,
    serialized_options=None,
  ),
])
_sym_db.RegisterServiceDescriptor(_CURRENCYSERVICE)

DESCRIPTOR.services_by_name['CurrencyService'] = _CURRENCYSERVICE

# @@protoc_insertion_point(module_scope)
