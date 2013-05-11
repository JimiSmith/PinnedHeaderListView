PinnedHeaderListView
================

This library provides a sectioned ListView with pinned headers. It looks and feels much like the default contacts app does on Android 4.0 and above

A custom adapter is provided which must be extended and used with the custom ListView.

The usage of this library is simple. You need to create an adapter that extends SectionedBaseAdapter.

There are 6 methods that need to be overridden:

* ```public Object getItem(int section, int position);```
* ```public long getItemId(int section, int position);```
* ```public int getSectionCount();```
* ```public int getCountForSection(int section);```
* ```public View getItemView(int section, int position, View convertView, ViewGroup parent);```
* ```public View getSectionHeaderView(int section, View convertView, ViewGroup parent);```

```getItemView``` and ```getSectionHeaderView``` should be treated as you would the ```getItemView``` method from a normal Adapter.
The same goes for ```getItem``` and ```getItemId```.

```getSectionCount``` and ```getCountForSection ```replace the ```getCount()``` method from a standard adapter.
You should return the number of sections in your list in ```getSectionCount``` and the number of items in a section in ```getCountForSection```.

In addition to these methods, there are a few others you may override:

* ```public int getItemViewType(int section, int position);```
* ```public int getItemViewTypeCount();```
* ```public int getSectionHeaderViewType(int section);```
* ```public int getSectionHeaderViewTypeCount();```

These replace the ```getViewTypeCount() and getItemViewType(int position)``` methods from a standard adapter

Note that you can return the same ItemViewType for a header and a non header and these will be cached seperately by the underlying ListView.  
That is, you will never get a header view passed in as the convertView in ```public View getItemView(int section, int position, View convertView, ViewGroup parent);```,
nor will you get an item view passed in as the convertView in ```public View getSectionHeaderView(int section, View convertView, ViewGroup parent);```

See the provided example for more details.

License
-------
Copyright (c) 2012, James Smith  
All rights reserved.  

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:  
* Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimer.  
* Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.  
* Neither the name of the <organization> nor the
  names of its contributors may be used to endorse or promote products
  derived from this software without specific prior written permission.  

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL James Smith BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
