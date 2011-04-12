% Groove Prolog Interface
% Copyright (C) 2009 Michiel Hendriks, University of Twente
% 
% This library is free software; you can redistribute it and/or
% modify it under the terms of the GNU Lesser General Public
% License as published by the Free Software Foundation; either
% version 2.1 of the License, or (at your option) any later version.
% 
% This library is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
% Lesser General Public License for more details.
% 
% You should have received a copy of the GNU Lesser General Public
% License along with this library; if not, write to the Free Software
% Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

% Documentation reading guide:
% + 	The argument shall be instantiated.
% ? 	The argument shall be instantiated or a variable.
% @ 	The argument shall remain unaltered.
% - 	The argument shall be a variable that will be instantiated

:-ensure_loaded(resource('/groove/prolog/builtin/groove.graph.pro')).
:-ensure_loaded(resource('/groove/prolog/builtin/groove.lts.pro')).
:-ensure_loaded(resource('/groove/prolog/builtin/groove.trans.pro')).
:-ensure_loaded(resource('/groove/prolog/builtin/groove.algebra.pro')).
:-ensure_loaded(resource('/groove/prolog/builtin/groove.type.pro')).
:-ensure_loaded(resource('/groove/prolog/builtin/groove.rule.pro')).